var ThreadWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'Thread Total Widget',
		this.TemplateName = 'thread-widget-template',
		this.ChartControlName = 'thread-widget-chart',
		this.chartTitleText = 'Total Count',
        this.yAxisTitle = 'Count',
		this.initWidget(),
        this.unit = '个'

	  }
});

var ThreadActiveWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'Thread Active Widget',
		this.TemplateName = 'thread-widget-template',
		this.ChartControlName = 'thread-widget-chart',
		this.chartTitleText = 'Active Count',
        this.yAxisTitle = 'Count',
		this.initWidget(),
        this.unit = '个'

	  }
});

var ThreadDeamonWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'Thread Deamon Widget',
		this.TemplateName = 'thread-widget-template',
		this.ChartControlName = 'thread-widget-chart',
		this.chartTitleText = 'Deamon Count',
      this.yAxisTitle = 'Count',
		this.initWidget(),
      this.unit = '个'

	  }
});

var ThreadIdleWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'Thread Idle Widget',
		this.TemplateName = 'thread-widget-template',
		this.ChartControlName = 'thread-widget-chart',
		this.chartTitleText = 'Idle Count',
      this.yAxisTitle = 'Count',
		this.initWidget(),
      this.unit = '个'

	  }
});

var ThreadInfoWidget = BaseInfoWidget.extend({

	  initialize : function() {
		this.Name = 'Thread Info Widget',
		this.TemplateName = 'threadsingleinfo-widget-template',
		this.initWidget()
	  }
});