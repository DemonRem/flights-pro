// Compiled by ClojureScript 1.9.521 {}
goog.provide('chart.controls');
goog.require('cljs.core');
goog.require('goog.string');
goog.require('goog.string.format');
chart.controls.format = (function chart$controls$format(var_args){
var args__23811__auto__ = [];
var len__23804__auto___28311 = arguments.length;
var i__23805__auto___28312 = (0);
while(true){
if((i__23805__auto___28312 < len__23804__auto___28311)){
args__23811__auto__.push((arguments[i__23805__auto___28312]));

var G__28313 = (i__23805__auto___28312 + (1));
i__23805__auto___28312 = G__28313;
continue;
} else {
}
break;
}

var argseq__23812__auto__ = ((((1) < args__23811__auto__.length))?(new cljs.core.IndexedSeq(args__23811__auto__.slice((1)),(0),null)):null);
return chart.controls.format.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__23812__auto__);
});

chart.controls.format.cljs$core$IFn$_invoke$arity$variadic = (function (fmt,args){
return cljs.core.apply.call(null,goog.string.format,fmt,args);
});

chart.controls.format.cljs$lang$maxFixedArity = (1);

chart.controls.format.cljs$lang$applyTo = (function (seq28309){
var G__28310 = cljs.core.first.call(null,seq28309);
var seq28309__$1 = cljs.core.next.call(null,seq28309);
return chart.controls.format.cljs$core$IFn$_invoke$arity$variadic(G__28310,seq28309__$1);
});

chart.controls.by_id = (function chart$controls$by_id(id){
return document.getElementById(id);
});
chart.controls.set_html_BANG_ = (function chart$controls$set_html_BANG_(id,msg){
return chart.controls.by_id.call(null,id).innerHTML = msg;
});
chart.controls.mouse_move = (function chart$controls$mouse_move(lat,lng){
return chart.controls.set_html_BANG_.call(null,"mousepos",[cljs.core.str.cljs$core$IFn$_invoke$arity$1("lat "),cljs.core.str.cljs$core$IFn$_invoke$arity$1(chart.controls.format.call(null,"%.4f",lat)),cljs.core.str.cljs$core$IFn$_invoke$arity$1(" lon "),cljs.core.str.cljs$core$IFn$_invoke$arity$1(chart.controls.format.call(null,"%.4f",lng))].join(''));
});
chart.controls.show_chart_controls = (function chart$controls$show_chart_controls(){
chart.controls.set_html_BANG_.call(null,"commands-header","<h4>Commands</h4>");

return chart.controls.set_html_BANG_.call(null,"commands","<select onchange='javascript:chart.client.command(this.value)'>\n   <option value='commands'>Commands</option>\n   <option value='watch-visible'>Watch Visible Area</option>\n   <option value='move-to'>Move to Airport</option>\n   <option value='schedule'>Schedule Flight</option>\n   <option value='wikipedia'>Toggle Wikipedia</option>\n   <option value='question'>Ask Question</option>\n   <option value='clear'>Clear</option>\n   </select>");
});
chart.controls.display_zoom = (function chart$controls$display_zoom(z){
return chart.controls.set_html_BANG_.call(null,"zoom",[cljs.core.str.cljs$core$IFn$_invoke$arity$1("zoom "),cljs.core.str.cljs$core$IFn$_invoke$arity$1(z)].join(''));
});

//# sourceMappingURL=controls.js.map