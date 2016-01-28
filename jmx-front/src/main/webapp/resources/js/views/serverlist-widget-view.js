
var ServerListWidget = BaseNoFreshWidget.extend({

	events : {
    "click .applist" : "clickApp"
        },
  initialize : function() {

    this.Name = "ServerList Widget"
    var projectitem = $("#appgroup").find("option:selected").attr("value");
	var appitem = $("#application").find("option:selected").attr("value");
	var params = {project:projectitem,app:appitem};
    this.load(params);

    // templates
    var templateSource        = $("#serverlist-widget-template").html()
    this.template         = Handlebars.compile(templateSource)
    this.$el.empty().html(this.template())

  }

, clickApp : function(e){
	var tr = $(e).attr("currentTarget");
	var trcontrol = "#" + $(tr).attr("target");
	   $(".applist").removeAttr("style");
	   $(tr).css("background-color","#7cc5e5");
	   if($(trcontrol).css("display")=="none"){
	     $(trcontrol).css("display","table-row");
		 $(tr).find("td div:first").removeClass().addClass("fa-chevron-down");
		 }
	   else{
	   $(trcontrol).css("display","none");
	   $(tr).find("td div:first").removeClass().addClass("fa-chevron-right");
	   }
}

})