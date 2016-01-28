var CpuUsageWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'CPU Usage Widget',
		this.TemplateName = 'detail-cpu-widget-template',
		this.ChartControlName = 'detail-cpu-widget-chart',
		this.chartTitleText = 'Usage',
        this.yAxisTitle = 'Usage',
		this.initWidget(),
        this.unit = '%';

	  }
});

var CpuInfoWidget = BaseInfoWidget.extend({
	  initialize : function() {
		this.Name = 'CPU Info Widget',
		this.TemplateName = 'detail-cpuinfo-widget-template',
		this.initWidget()
	  }
});