var DBActiveCountWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'DB Active Count Widget',
		this.TemplateName = 'detail-database-widget-template',
		this.ChartControlName = 'detail-database-widget-chart',
		this.chartTitleText = 'Database Active Count',
        this.yAxisTitle = 'count',
		this.initWidget(),
        this.unit = '个'

	  }
});

var DBIdleCountWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'DB IDle Count Widget',
		this.TemplateName = 'detail-database-widget-template',
		this.ChartControlName = 'detail-database-widget-chart',
		this.chartTitleText = 'Database Idle Count',
        this.yAxisTitle = 'count',
		this.initWidget(),
        this.unit = '个'
	  }
});

var DBFailCountWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'DB Fail Count Widget',
		this.TemplateName = 'detail-database-widget-template',
		this.ChartControlName = 'detail-database-widget-chart',
		this.chartTitleText = 'Database Fail Connection Count',
        this.yAxisTitle = 'count',
		this.initWidget(),
        this.unit = '个'
	  }
});

var DBInfoWidget = BaseInfoWidget.extend({
	  initialize : function() {
		this.Name = 'Database Info Widget',
		this.TemplateName = 'detail-databaseinfo-widget-template',
		this.initWidget()
	  }
});