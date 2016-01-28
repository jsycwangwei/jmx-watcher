var BaseWidget = Backbone.View.extend({

  enableLogging : false

, updateFrequency : 1000

, Name : "BaseWidget"

,param : {}//外界传入参数

, init : function () {

      var self = this;

      this.timer = setInterval( function () { self.UpdateModel(true) }, this.updateFrequency )

      // set event listners
      this.model
        .on("error", this.error, this)
        .on("change", this.ModelChanged, this)

  }

, UpdateModel : function (enableTimer) {

	clearInterval(this.timer)

    this.startTime = new Date()

	if(!this.param){
		this.model.fetch({
	        data : this.param
	    })
	}else{
	    this.model.fetch({
	        data : {from : this.$el.find('[name=from]').val()
	    	    , to : this.$el.find('[name=to]').val()
	    	    , server : $("input[name=server]").val()
	    	    ,app:$("input[name=app]").val()}
	    })
	}

    this.enableTimer = enableTimer;

  }

, ModelChanged : function(){
    this.endTime = new Date();
    var timeElapsed = (this.endTime - this.startTime);

    if (this.enableLogging){
     console.log(this.Name + ": Time Elapsed = " + timeElapsed + " ms")
    }
     var self = this
     this.render()
     if(this.enableTimer){
     this.timer = setInterval( function () { self.UpdateModel(true) }, this.updateFrequency )
     }
}
, SelfDefineParam : function(params){//自定义参数
	this.param = params;
}
,Refresh : function(){//刷新，立即执行
	this.UpdateModel(true);
}
, Go : function( el ) {
    this.UpdateModel(false)
  }
,
ChangeTimeFrame : function ( el ) {

    var selectionType = $(el.target).data("type")
      , timeFrame = parseInt( $(el.target).data("time") )

    // update the dropdown's label
    $(el.target)
      .closest(".btn-group")
      .children()
      .first()
      .text($(el.target).text())

    // Custom time frame selected
    if ( selectionType == "custom" ) {
      $(el.target)
        .closest(".btn-group")
        .siblings(".date-control")
        .css("display","inline")
    }
    // real time
    else if ( selectionType == "realtime" ) {
      $(el.target)
        .closest(".btn-group")
        .siblings(".date-control")
        .css("display","none")

      var self = this
      this.$el.find('[name=from]').val("")
      this.$el.find('[name=to]').val("")
      self.UpdateModel(true)
      this.timer = setInterval( function () { self.UpdateModel(true) }, this.updateFrequency )
    }
    // one of the template time frame selected
    // example: last 15mins, last 1 day etc
    else {

      $(el.target)
        .closest(".btn-group")
        .siblings(".date-control")
        .css("display","none")

      var endDate = new Date()
        , startDate = endDate

      switch(selectionType) {

        case 'minute' :
          startDate = new Date(endDate - timeFrame * 60000)
          break

        case 'hour' :
          startDate = new Date(endDate - timeFrame * 60*60000)
          break

        case 'day' :
          startDate = new Date(endDate - timeFrame * 24*60*60000)
          break

        case 'week' :
          startDate = new Date(endDate - timeFrame * 7*24*60*60000)
          break

        case 'month' :
          startDate = new Date(endDate - timeFrame * 30*24*60*60000)
          break
      }

      this.$el.find('[name=from]').val(this.ISODateString(startDate))
      this.$el.find('[name=to]').val(this.ISODateString(endDate))
      this.UpdateModel(false)

    }
  }
,
  ISODateString : function ( d ) {

	    function pad ( n ) {
	      return n < 10 ? '0'+n : n
	    }

	    return d.getFullYear()+'-'
	         + pad(d.getMonth()+1)+'-'
	         + pad(d.getDate())+' '
	         + pad(d.getHours())+':'
	         + pad(d.getMinutes())+':'
	         + pad(d.getSeconds())
	  }
, error: function ( model, error ) {
   if (this.enableLogging)
      console.log(this.Name + ": Error Occured \n" + error + "\n" + model )

  }

})