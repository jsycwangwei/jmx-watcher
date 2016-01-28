
var BaseWarningLineWidget = BaseWidget.extend({

	events : {
	    "click .time-period" : "ChangeTimeFrame"
	    	, "click .go" : "Go"
	        },
  initWidget : function() {

    this.Name = "Base Warning Line Widget"
    this.init()
    this.updateFrequency = 10000 // every 10 seconds
    this.unit = 'M';
    var self = this;
    this.obj = this;//对外暴露一个自身对象给子类使用
    var option = $("#select");
    this.plotline = [];
    this.limit = '';
    if(option.length >= 1){
    	this.limit = option.find("option:selected").attr("limit");
    	if(this.limit.trim()!=''){
    	this.plotline = [{
            color: '#FF0000',//设置数据块的颜色
            width: 2,
           value: this.limit,
            label: {
                text: 'Limit:' + this.limit,
                style: {color: 'red',font:'normal 11px Verdana, sans-serif' },//设置数据块对应字体颜色
                align: 'left',
                textAlign:'left',
                verticalAlign:'bottom'
            }
          }]
    	}
    }

    // templates
    var templateName = "#" + this.TemplateName;
    var chartName = "#" + this.ChartControlName;
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
          plotLines: this.plotline,
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
      series: [{
          name: 'Current',
          marker: {
              symbol: 'square'
          },
          data: []

      }],
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
//var ptStart=new Date();
  $.each(model.data, function(index, obj){
    var recordDate = new Date(obj[0][0], obj[0][1], obj[0][2], obj[0][3], obj[0][4], obj[0][5]);
	xAxis_v.push(format(recordDate,'yyyy-MM-dd HH:mm:ss'));
	var cur_v=obj[1]
    if(self.limit!=''&&cur_v >= self.limit){
    	cur_v = {y:cur_v,color:'#FF0000'};
    }
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


	chart.series[0].update(
		{
            name: 'Current',
            data: current

        }
	);


}
})