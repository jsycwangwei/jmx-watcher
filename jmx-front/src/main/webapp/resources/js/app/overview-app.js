

var OverviewApp = {

    init: function() {
	    HandlerbarHelper.RegisterHelpers();
        this.topEventWidget = new TopEventWidget({
            el : $("#topevent-widget-placeholder")
          , model : new TopEventWidgetModel()
        })

        this.jmxFailConnWidget = new JmxFailConnWidget({
            el : $("#connfail-widget-placeholder")
          , model : new OverviewJmxFailConnWidgetModel()
        })

        this.overviewDBWidget = new OverviewDBWidget({
            el : $("#overviewdb-widget-placeholder")
          , model : new OverviewDBWidgetModel()
        })

        this.overviewMemoryWidget = new OverviewMemoryWidget({
            el : $("#overviewmemory-widget-placeholder")
          , model : new OverviewMemoryWidgetModel()
        })

        this.overviewThreadWidget = new OverviewThreadWidget({
            el : $("#overviewthread-widget-placeholder")
          , model : new OverviewThreadWidgetModel()
        })

    }
}
