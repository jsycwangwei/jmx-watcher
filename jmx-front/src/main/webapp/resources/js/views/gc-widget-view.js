var GCCollectionSizeWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'GC CollectionSize Widget',
		this.TemplateName = 'detail-gc-widget-template',
		this.ChartControlName = 'detail-gc-widget-chart',
		this.chartTitleText = 'Collection Size',
        this.yAxisTitle = 'collection space',
		this.initWidget(),
        this.unit = 'KB'

	  }
});

var GCDurationWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'GC Duration Widget',
		this.TemplateName = 'detail-gc-widget-template',
		this.ChartControlName = 'detail-gc-widget-chart',
		this.chartTitleText = 'GC Duration',
        this.yAxisTitle = 'time',
		this.initWidget(),
        this.unit = 'ms'
	  }
});

var GCInfoWidget = BaseInfoWidget.extend({
	  initialize : function() {
		this.Name = 'GC Info Widget',
		this.TemplateName = 'detail-gcinfo-widget-template',
		this.initWidget()
	  }
});