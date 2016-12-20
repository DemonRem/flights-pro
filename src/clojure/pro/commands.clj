(ns pro.commands
(:use protege.core)
(:require [compojure.core :refer [GET]]
              [fr24.client :as fr24]
              [async.proc :as asp]
              [rete.core :as rete]))

(def HOST "http://localhost:")
(def PORT 4444)
(def URL {:base (str HOST PORT "/")
 :chart (str HOST PORT "/chart/")
 :directives (str HOST PORT "/directives/")
 :instructions (str HOST PORT "/instructions/")
 :command (str HOST PORT "/command/")})
(def CHN {:answer (asp/mk-chan)
 :directives (asp/mk-chan)
 :instructions (asp/mk-chan)
 :czml (asp/mk-chan)})
(def TIM {:popup 30000
 :trail 30000})
(defn current-time []
  (int (/ (System/currentTimeMillis) 1000)))

(defn process-flights [fls]
  (let [crt (current-time)
       fls (seq @fls)]
  (println crt (count fls))
  (rete/assert-frame ['Current 'time crt])
  (doseq [[k v] fls]
    (let [alt (fr24/altitude v)]
      (rete/assert-frame 
	['Flight
	'id k
	'callsign (fr24/callsign v)
	'coord (fr24/coord v)
	'course (fr24/course v)
	'speed (fr24/speed v)
	'altitude alt
	'time crt
	'status (if (> alt 0)
                                     "LEVEL"
                                     "GROUND")])))
  (rete/fire)
  true))

(defn clear
  ([params]
  (clear))
([]
  (fr24/stop)
  (fr24/clear-flights)
  (rete/reset)
  (asp/pump-in (:instructions CHN)
      {:instruct :clear})
  ""))

(defn make-info-html [call img dat]
  (let [head (str "<h3>" call "</h3>")
       itag (str "<img src=\"" img "\">")
       rows (for [[k v] dat]
                 (str "<tr><td>" k "</td><td>" v "</td></tr>"))
      rows (apply str rows)]
  (str head itag "<table>" rows "</table>")))

(defn watch-visible
  ([]
  (let [[n s w e] @fr24/BBX]
    (watch-visible {:n n :s s :w w :e e})))
([params]
  (println [:WATCH-VISIBLE params])
  (let [{:keys [n s w e]} params]
    (clear)
    (fr24/set-bbx n s w e)
    (fr24/start process-flights)
    "")))

(defn update-watch-area []
  (if (= @fr24/STATUS "RUN")
  (watch-visible)))

(defn do-trail [id head]
  (let [pts (if-let [inf (fr24/fl-info id)]
               (mapcat #(list (% "lat") (% "lng") (% "alt")) (inf "trail"))
               head)]
  (asp/pump-in (:instructions CHN)
        {:instruct :trail
         :id id
         :points pts
         :options {:weight 3
                        :color "purple"}
         :time (:trail TIM)})
  ""))

(defn set-map-view [coord]
  (asp/pump-in (:instructions CHN)
	{:instruct :map-center
	 :coord coord}))

(defn info [params]
  (let [id (:id params)]
  (if-let [inf (fr24/fl-info id)]
    (let [cal (fr24/callsign id)
           apt (inf "airport")
           acr (inf "aircraft")
           tim (inf "time")
           img (get (first (get-in acr ["images" "thumbnails"])) "src")
           [lat lon] (fr24/coord id)
           dat [["from" (or (get-in apt ["origin" "name"]) "-")]
                  ["to" (or (get-in apt ["destination" "name"]) "-")]
                  ["airline" (or (get-in inf ["airline" "short"]) "-")]
                  ["real-departure" (or (get-in tim ["real" "departure"]) "-")]
                  ["scheduled-arrival" (or (get-in tim ["scheduled" "arrival"]) "-")]
                  ["aircraft" (or (get-in acr ["model" "text"]) "-")]
                  ["latitude" (or lat "-")]
                  ["longitude" (or lon "-")]
                  ["course" (or (fr24/course id) "-")]
                  ["speed" (or (fr24/speed id) "-")]
                  ["altitude" (or (fr24/altitude id) "-")]
                  [(str "<input type='button' style='color:purple' value='Trail'
                             onclick='chart.client.trail(\"" id "\")' >")
                   (str "<input type='button' style='color:blue' value='Follow'
                             onclick='chart.client.follow(\"" id "\")' >")]
                  [""
                   "<input type='button' style='color:red' value='Stop'
                       onclick='chart.client.stopfollow()' >"]]
           htm (make-info-html cal img dat)]
      (asp/pump-in (:instructions CHN)
        {:instruct :popup
         :id (:id params)
         :html htm
         :time (:popup TIM)}))))
"")

(defn onboard [params]
  (println [:PARAMS params])
(let [cls (:callsign params)]
  (condp = cls
    "manual" (do
                     (asp/pump-in (:directives CHN)
	{:directive :manual})
                     (rete/assert-frame ['Onboard 'callsign "STOP"]))
   "select" (let [lst (vec (sort (map fr24/callsign (keys @fr24/FLIGHTS))))]
                   (asp/pump-in (:directives CHN)
	{:directive :callsigns
	 :list lst}))
    (rete/assert-frame ['Onboard 'callsign cls 'time 0]))
  ""))

(defn terrain [params]
  "yes")

(defn follow [params]
  (println [:PARAMS params])
(let [id (:id params)]
  (if (fr24/dat id)
    (rete/assert-frame ['Follow 'id id 'time 0]))))

(defn visible [params]
  (let [{:keys [n s w e]} params]
  (fr24/set-bbx n s w e)
  ""))

(defn trail [params]
  (println [:PARAMS params])
(do-trail (:id params) []))

(defn stopfollow [params]
  (rete/assert-frame ['Follow 'id "STOP" 'time 0]))

