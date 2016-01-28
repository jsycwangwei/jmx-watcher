var MemoryHeapWidget = BaseWarningLineWidget.extend({

  initialize : function() {
	this.Name = 'Memory Heap Widget',
	this.TemplateName = 'detail-memory-widget-template',
	this.ChartControlName = 'detail-memory-widget-chart',
	this.chartTitleText = 'Heap大小',
	this.yAxisTitle = 'space used',
	this.initWidget()

  }
});

var MemoryEdenWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'Memory Eden Widget',
		this.TemplateName = 'detail-memory-widget-template',
		this.ChartControlName = 'detail-memory-widget-chart',
		this.chartTitleText = 'Eden大小',
		this.yAxisTitle = 'space used',
		this.initWidget()

	  }
});

var MemorySurvivorWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'Memory Survivor Widget',
		this.TemplateName = 'detail-memory-widget-template',
		this.ChartControlName = 'detail-memory-widget-chart',
		this.chartTitleText = 'Survivor大小',
		this.yAxisTitle = 'space used',
		this.initWidget()
	  }
});

var MemoryOldWidget = BaseWarningLineWidget.extend({

	  initialize : function() {
		this.Name = 'Memory Old Widget',
		this.TemplateName = 'detail-memory-widget-template',
		this.ChartControlName = 'detail-memory-widget-chart',
		this.chartTitleText = 'Old大小',
		this.yAxisTitle = 'space used',
		this.initWidget()
	  }
});
var MemoryInfoWidget = BaseInfoWidget.extend({

	  initialize : function() {
		this.Name = 'Memory Info Widget',
		this.TemplateName = 'detail-memoryinfo-widget-template',
		this.initWidget()
	  }
});