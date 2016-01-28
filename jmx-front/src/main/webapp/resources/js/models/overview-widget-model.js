var TopEventWidgetModel = Backbone.Model.extend({

    url: '/api/overview/topevent',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var OverviewJmxFailConnWidgetModel = Backbone.Model.extend({

    url: '/api/overview/jmxFailConn',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var OverviewDBWidgetModel = Backbone.Model.extend({

    url: '/api/overview/dbactivecount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var OverviewThreadWidgetModel = Backbone.Model.extend({

    url: '/api/overview/threadactivecount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var OverviewMemoryWidgetModel = Backbone.Model.extend({

    url: '/api/overview/mheapcount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})