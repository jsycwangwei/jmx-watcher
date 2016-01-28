var TopEventWidget = BaseInfoWidget.extend({

  initialize : function() {
	this.Name = 'Top Event Widget',
	this.TemplateName = 'topevent-widget-template',
	this.initWidget()

  }
});

var JmxFailConnWidget = BaseNoFreshWidget.extend({

	  initialize : function() {
		this.Name = 'JMX FailConn Widget'
		var projectitem = $("#appgroup").find("option:selected").attr("value");
		var appitem = $("#application").find("option:selected").attr("value");
		var currentPage = 1;
		var params = {project:projectitem,app:appitem,currentpage:currentPage};
	    this.load(params);
	    this.pagination = true;
	    this.paginationControl = "connfail-pagination";
	    var templateSource        = $("#connectfail-widget-template").html();
	    this.template         = Handlebars.compile(templateSource)
	    this.$el.empty().html(this.template())

	  }
	});

var OverviewDBWidget = BaseWarningLineWidget.extend({
	initialize : function() {
	this.Name = 'Overview DB Widget',
	this.TemplateName = 'overview-db-widget-template',
	this.ChartControlName = 'overview-database-widget-chart',
	this.chartTitleText = '(All Servers) Active Connection',
	this.yAxisTitle = 'count',
	this.initWidget(),
	this.unit = '个'

  },
  render: function(){
	  overviewrender(this.obj);//overviewrender方法定义在jwatcher.js文件中
  }
});

var OverviewMemoryWidget = BaseWarningLineWidget.extend({
	initialize : function() {
	this.Name = 'Overview Memory Widget',
	this.TemplateName = 'overview-memory-widget-template',
	this.ChartControlName = 'overview-memory-widget-chart',
	this.chartTitleText = '(All Servers)Memory Heap',
	this.yAxisTitle = 'space used',
	this.initWidget()

  },
  render: function(){//覆盖父类的同名方法
	  overviewrender(this.obj);
  }
});

var OverviewThreadWidget = BaseWarningLineWidget.extend({
	initialize : function() {
	this.Name = 'Overview Thread Widget',
	this.TemplateName = 'overview-thread-widget-template',
	this.ChartControlName = 'overview-thread-widget-chart',
	this.chartTitleText = '(All Servers)Thread Active',
	this.yAxisTitle = 'count',
	this.initWidget(),
	this.unit = '个'

  },
  render: function(){
	  overviewrender(this.obj);
  }
});


