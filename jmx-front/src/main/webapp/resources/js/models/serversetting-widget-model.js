var ServerSettingWidgetModel = Backbone.Model.extend({

    url: '/api/serversetting',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})