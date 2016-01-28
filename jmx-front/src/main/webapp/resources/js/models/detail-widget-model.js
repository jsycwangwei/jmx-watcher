//--------Memory--------------
var MemoryHeapWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/heap',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var MemoryInfoWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/memoryinfo',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var MemoryEdenWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/eden',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var MemorySurvivorWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/survivor',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var MemoryOldWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/oldsize',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})
//--------Thread--------------
var ThreadWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/threadtotalcount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var ThreadActiveWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/threadActivecount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var ThreadIdleWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/threadIdlecount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var ThreadDeamonWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/threadDeamoncount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var ThreadInfoWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/threadinfo',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

//--------GC--------------
var GCDurationWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/gcduration',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var GCCollectionSizeWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/gccollectionsize',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var GCInfoWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/gcinfo',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})
//--------DB--------------
var DBActiveWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/dbactivecount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var DBIdleWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/dbidlecount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var DBFailWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/dbfailcount',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var DBInfoWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/dbinfo',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

//--------Dump--------------
var DumpWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/dump',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

//--------failconn--------------
var FailConnWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/failconn',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var CpuUsageWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/cpuusage',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})

var CpuInfoWidgetModel = Backbone.Model.extend({

    url: '/api/detail/main/cpuinfo',

    initialize: function() {
    },

    defaults: {
    	"databases" : {}
    }

})
