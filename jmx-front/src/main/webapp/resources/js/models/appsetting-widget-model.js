var AppSettingWidgetModel = Backbone.Model.extend({

    url: '/api/appsetting',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})