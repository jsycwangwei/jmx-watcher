
var BaseMultiLineWidget = BaseWidget.extend({

	events : {
	    "click .time-period" : "ChangeTimeFrame"
	    	, "click .go" : "Go"
	        },
  initWidget : function() {

    this.Name = "Base Multi Line Widget"
    this.init()
    this.updateFrequency = 10000 // every 10 seconds
    this.unit = 'M';
    var self = this;
    // templates
    var templateName = "#" + this.TemplateName;
    var chartName = "#" + this.ChartControlName;
    var xyTemplate = "#multiline-widget-template";
    var xySource        = $(xyTemplate).html().trim()
    this.xyJson = eval("("+xySource+")");
    var templateSource        = $(templateName).html()
    this.template         = Handlebars.compile(templateSource)
      this.$el.empty().html(this.template())
      this.chart=$(chartName).highcharts({
          chart: {
          type: 'spline'
      },
      title: {
          text: this.chartTitleText
      },
      xAxis: {
          categories: []
      },
      yAxis: {
          title: {
              text: this.yAxisTitle
          },
          labels: {
              formatter: function() {
                  return this.value + self.unit
              }
          },
          allowDecimals:false
      },
      tooltip: {
          crosshairs: true,
          shared: true
      },
      plotOptions: {
          spline: {
              marker: {
                  radius: 4,
                  lineColor: '#666666',
                  lineWidth: 1
              }
          }
      },
      series: this.xyJson,
      credits:{enabled:false}
  });
  }

, render: function() {

	var model = this.model.toJSON()
    , markUp = this.template(model)
    , self = this;

  //self.dataTable.removeRows(0,self.dataTable.getNumberOfRows())
  var xAxis_v=new Array();
  var current=new Array();
  var max=new Array();
//var ptStart=new Date();
  $.each(model.data, function(index, obj){
    var recordDate = new Date(obj[0][0], obj[0][1], obj[0][2], obj[0][3], obj[0][4], obj[0][5]);
	xAxis_v.push(format(recordDate,'yyyy-MM-dd HH:mm:ss'));
	var cur_v=obj[1]
	current.push(cur_v);

  });

  var chart=self.chart.highcharts();
  chart.xAxis[0].update({
	categories: xAxis_v,
		   labels:{
		   enabled: model.data.length>30?false:true
	}
  })

  chart.yAxis[0].update({
	labels:{
				format:'{value} '+ self.unit
	}
  })

$.each(this.xyJson, function(index, obj){}
	chart.series[0].update(
		{
            name: obj.name,
            data: current

        }
	);


}
})