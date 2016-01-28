
var DetailFailConnWidget = BaseNoFreshWidget.extend({

  initialize : function() {

    this.Name = "FailConn Widget"
    var host = $("input[name=server]").val();
	var app = $("input[name=app]").val();
	var currentPage = 1;
	var params = {server:host,app:app,currentpage:currentPage};
    this.load(params);
    this.pagination = true;//是否支持分页
    this.paginationControl = "connfail-pagination";
    // templates
    var templateSource        = $("#detail-connfail-widget-template").html()
    this.template         = Handlebars.compile(templateSource)
    this.$el.empty().html(this.template())

  }//,
//  afterRender : function(){
//	  $("[data-toggle='popover']").popover();
//  }

})