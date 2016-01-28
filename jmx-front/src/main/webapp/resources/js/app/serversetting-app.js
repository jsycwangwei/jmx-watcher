var ServerSettingApp = {
    init: function() {
        this.serverSettingWidget = new ServerSettingWidget({
            el : $("#serversetting-widget-placeholder")
          , model : new ServerSettingWidgetModel()
        })
    }
}
