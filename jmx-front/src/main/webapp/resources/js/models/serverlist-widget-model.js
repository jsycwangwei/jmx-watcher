var ServerListWidgetModel = Backbone.Model.extend({

    url: '/api/serverlist',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})