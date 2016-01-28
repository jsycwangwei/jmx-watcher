var ServerListApp = {
    init: function() {
    	HandlerbarHelper.RegisterHelpers();
        this.serverListWidget = new ServerListWidget({
            el : $("#serverlist-widget-placeholder")
          , model : new ServerListWidgetModel()
        })
    }
}