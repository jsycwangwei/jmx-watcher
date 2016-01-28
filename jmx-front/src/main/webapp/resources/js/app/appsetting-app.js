var ServerSettingApp = {
    init: function() {
	HandlerbarHelper.RegisterHelpers();
        this.appSettingWidget = new AppSettingWidget({
            el : $("#appsetting-widget-placeholder")
          , model : new AppSettingWidgetModel()
        })
    }
}