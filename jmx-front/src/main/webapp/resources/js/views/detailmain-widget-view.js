
var DetailMainWidget = BaseWidget.extend({

  initialize : function() {

    this.Name = "Detail Main Widget"

    this.init()
    this.updateFrequency = 10000 // every 5 seconds

    // templates
    var templateSource        = $("#memory-widget-template").html()
    this.template         = Handlebars.compile(templateSource)
      this.$el.empty().html(this.template())
      this.chart=$('#memory-widget-chart').highcharts({
			chart:{
			type:'spline',
			spacingRight:0,
			reflow:false,
			height:200
			},
          title: {
              text: '',
              x: -20 //center
          },
          subtitle: {
              text: '',
              x: -20
          },
          xAxis: {
             categories: [],
			   labels:{
			   enabled: false
			   }
          },
          yAxis: {
              title: {
                  text: 'memory used'
              },
              plotLines: [{
                  value: 0,
                  width: 1,
                  color: '#808080'
              }],
				labels:{
					format:'{value} '
				}

          },
          tooltip: {
              valueSuffix: ''
          },
          legend: {
              layout: 'vertical',
              align: 'right',
              verticalAlign: 'middle',
              borderWidth: 0
          },
          series: [
			{
				name:"max",
				data:[0]
			},
			{
			name:"current",
			data:[0],
			}]
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
  var unit='M';
  //var ptStart=new Date();
  $.each(model.data, function(index, obj){
      // first item of the object contains datetime info
      // [ YYYY, MM, DD, HH, MM, SS ]
    var recordDate = new Date(obj[0][0], obj[0][1]-1, obj[0][2], obj[0][3], obj[0][4], obj[0][5]);
	xAxis_v.push(format(recordDate,'yyyy-MM-dd HH:mm:ss'));
	  /*
	  if(index==0){
		ptStart=recordDate;
	  }
	  if(model.data.length<30){

	  }else{
		xAxis.push('');
	  }

      if(self.dataTable)
        self.dataTable.addRow( [recordDate, obj[1], obj[2]] )
      */
	var max_v=obj[1];
	var cur_v=obj[2]
	if(obj[1].length<4){
		if(index==0){
			unit='byte';
		}
	}else if(obj[1].length<6){
		if(index==0){
			unit='K';
		}
		max_v=parseFloat((parseFloat(max_v)/1024).toFixed(2));
		cur_v=parseFloat((parseFloat(cur_v)/1024).toFixed(2));
	}else{
		max_v=parseFloat((parseFloat(max_v)/1024/1024).toFixed(2));
		cur_v=parseFloat((parseFloat(cur_v)/1024/1024).toFixed(2));
	}
	max.push(max_v);
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
				format:'{value} '+unit
	}
  })


	//chart.yAxis.labels={format:'{value} '+unit};
	chart.series[0].update(
		{
            name: 'max',
            data: max
        }
	);
	chart.series[1].update(
		{
            name: 'Current',
            data: current

        }
	);


}
})