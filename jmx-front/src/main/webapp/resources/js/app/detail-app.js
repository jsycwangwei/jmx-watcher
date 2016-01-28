

var DetailMainApp = {

    init: function() {

        var detailMainWidget = new DetailMainWidget({
            el : $("#memory-widget-placeholder")
          , model : new DetailMainWidgetModel()
        })

    }

  }

var DetailFailConnApp = {

	    init: function() {

	        this.detailFailConnWidget = new DetailFailConnWidget({
	            el : $("#detail-connfail-widget-placeholder")
	          , model : new FailConnWidgetModel()
	        })

	    }

	  }

var DetailMemoryApp = {
	    init: function(type) {
	        HandlerbarHelper.RegisterPartials();
	        HandlerbarHelper.RegisterHelpers();
	        this.placeholder = "#detail-memory-widget-placeholder";
	        this.changeWidget(type);
	        var memoryInfoWidget = new MemoryInfoWidget({
	            el : $("#detail-memoryinfo-widget-placeholder")
		          , model : new MemoryInfoWidgetModel()
		        })

	    },
	    changeWidget: function(type){
	    	if(this.memoryWidget){//如果对象已经存在
	        	clearInterval(this.memoryWidget.timer);//清除内部的定时器
	        }
	        if(type=="heap"){
		        this.memoryWidget = new MemoryHeapWidget({
		            el : $(this.placeholder)
		          , model : new MemoryHeapWidgetModel()
		        })
	        }
	        else if(type=="eden"){
	        	this.memoryWidget = new MemoryEdenWidget({
		            el : $(this.placeholder)
		          , model : new MemoryEdenWidgetModel()
		        })
	        }
	        else if(type=="survivor"){
	        	this.memoryWidget = new MemorySurvivorWidget({
		            el : $(this.placeholder)
		          , model : new MemorySurvivorWidgetModel()
		        })
	        }
	        else if(type=="old"){
	        	this.memoryWidget = new MemoryOldWidget({
		            el : $(this.placeholder)
		          , model : new MemoryOldWidgetModel()
		        })
	        }

	    }

	  }

var DetailGCApp = {
	    init: function(type) {
	        this.placeholder = "#detail-gc-widget-placeholder";
	        HandlerbarHelper.RegisterPartials();
	        HandlerbarHelper.RegisterHelpers();
	        this.changeWidget(type);
	        var gcInfoWidget = new GCInfoWidget({
	            el : $("#detail-gcinfo-widget-placeholder")
		          , model : new GCInfoWidgetModel()
		        })

	    },
	    changeWidget: function(type){
	    	if(this.gcWidget){//如果对象已经存在
	        	clearInterval(this.gcWidget.timer);//清除内部的定时器
	        }
	    	if(type=="collectionSize"){
		        this.gcWidget = new GCCollectionSizeWidget({
		            el : $(this.placeholder)
		          , model : new GCCollectionSizeWidgetModel()
		        })
	        }
	        else if(type=="duration"){
	        	this.gcWidget = new GCDurationWidget({
		            el : $(this.placeholder)
		          , model : new GCDurationWidgetModel()
		        })
	        }
	    }

	  }

var DetailDatabaseApp = {

	    init: function(type) {
	        this.placeholder = "#detail-database-widget-placeholder";
	        HandlerbarHelper.RegisterPartials();
	        HandlerbarHelper.RegisterHelpers();
	        this.changeWidget(type);
	        var databaseInfoWidget = new DBInfoWidget({
	            el : $("#detail-dbinfo-widget-placeholder")
	          , model : new DBInfoWidgetModel()
	        })
	    },
	    changeWidget: function(type){
	    	if(this.dbWidget){//如果对象已经存在
	        	clearInterval(this.dbWidget.timer);//清除内部的定时器
	        }
	    	if(type=="activeCount"){
		        this.dbWidget = new DBActiveCountWidget({
		            el : $(this.placeholder)
		          , model : new DBActiveWidgetModel()
		        })
	        }
	        else if(type=="idleCount"){
	        	this.dbWidget = new DBIdleCountWidget({
		            el : $(this.placeholder)
		          , model : new DBIdleWidgetModel()
		        })
	        }
	        else if(type=="failCount"){
	        	this.dbWidget = new DBFailCountWidget({
		            el : $(this.placeholder)
		          , model : new DBFailWidgetModel()
		        })
	        }
	    }

	  }

var DetailThreadApp = {

	    init: function(type) {
	        this.placeholder = "#thread-widget-placeholder";
	        HandlerbarHelper.RegisterPartials();
	        HandlerbarHelper.RegisterHelpers();
	        this.changeWidget(type);
	        var threadInfoWidget = new ThreadInfoWidget({
	            el : $("#threadsingleinfo-widget-placeholder")
	          , model : new ThreadInfoWidgetModel()
	        })


	    },
	    changeWidget: function(type){
	    	if(this.threadWidget){//如果对象已经存在
	        	clearInterval(this.threadWidget.timer);//清除内部的定时器
	        }
	    	if(type=="activeCount"){
	    		   this.threadWidget = new ThreadActiveWidget({
		            el : $(this.placeholder)
		          , model : new ThreadActiveWidgetModel()
		        })
	        }
	        else if(type=="idleCount"){
	        	this.threadWidget = new ThreadIdleWidget({
		            el : $(this.placeholder)
		          , model : new ThreadIdleWidgetModel()
		        })
	        }
	        else if(type=="totalCount"){
	        	this.threadWidget = new ThreadWidget({
		            el : $(this.placeholder)
		          , model : new ThreadWidgetModel()
		        })
	        }
	        else if(type=="deamonCount"){
	        	this.threadWidget = new ThreadDeamonWidget({
		            el : $(this.placeholder)
		          , model : new ThreadDeamonWidgetModel()
		        })
	        }
	    }

	  }

var DetailCPUApp = {

	    init: function(type) {
	        this.placeholder = "#detail-cpu-widget-placeholder";
	        HandlerbarHelper.RegisterPartials();
	        HandlerbarHelper.RegisterHelpers();
	        this.changeWidget(type);
	        var cpuInfoWidget = new CpuInfoWidget({
	            el : $("#detail-cpuinfo-widget-placeholder")
	          , model : new CpuInfoWidgetModel()
	        })


	    },
	    changeWidget: function(type){
	    	if(this.cpuWidget){//如果对象已经存在
	        	clearInterval(this.cpuWidget.timer);//清除内部的定时器
	        }
	    	if(type=="cpuusage"){
	    		   this.cpuWidget = new CpuUsageWidget({
		            el : $(this.placeholder)
		          , model : new CpuUsageWidgetModel()
		        })
	        }
	    }

	  }



