//overview界面图形收缩和展开
   $(".toggle").on('click',function(e){
   var target = $(this).attr('data-target');
   if($(target).css("visibility")=="hidden"){
     showPlaceHolder(target);
	 $(this).find(".actions a:last-child i:first").removeClass().addClass("fa-angle-double-down");
	 $.each($(target).find(".ignoreHeight"),function(){
		 showPlaceHolder(this);
	 })
	 }
   else{
   hidePlaceHolder(target);
   $(this).find(".actions a:last-child i:first").removeClass().addClass("fa-angle-double-up");
   $.each($(target).find(".ignoreHeight"),function(){
	   hidePlaceHolder(this);
	 })
   }
   })

//job配置界面的编辑
$(".operation").delegate('.editoper','click',function(){
	changeToEditable(this);
})
//job配置界面的取消
$(".operation").delegate(".canceledit",'click',function(){
	changeToCancel(this,"cancel");
})
//job配置界面的保存
$(".saveoper").on('click',function(){
	var tr = $(this).parent().parent();
	var savebtn = $(this);
	var cancelbtn = $(this).prev();
	var jobId = $(tr).find("td[name='jobid']").text().trim();
	var jobName = $(tr).find("td[name='jobname']").text().trim();
	var jobGroup = $(tr).find("td[name='jobgroup']").text().trim();
	var jobCronexpression = $(tr).find("td[name='jobcronexpression'] input").val();
	var jobStatus = $(tr).find("td[name='jobstatus'] select").find("option:selected").val()
	var jobDescription = $(tr).find("td[name='jobdescription'] input").val();
	var iSconcurrent = $(tr).find("td[name='isconcurrent'] select").find("option:selected").val();
	var springId = $(tr).find("td[name='springid'] input").val();
	var methodName = $(tr).find("td[name='methodname'] input").val();

	var param = {jobId:jobId,jobName:jobName,jobGroup:jobGroup,
			jobStatus:jobStatus,cronExpression:jobCronexpression,
			description:jobDescription,isConcurrent:iSconcurrent,springId:springId,methodName:methodName};
    var editArray = {};
	$.ajax({
			url:'/event/job/save',
			dataType:'json',
			type:"POST",
			asyn:false,
			data:param,
            success:function(data){
			   if(data.success){
				   changeToCancel(cancelbtn,"save");
				   replaceOrign(savebtn);
			   }else{
				   alert(data.msg);
			   }
			},
				error:function(msg){
					alert("请求有异常");
				}
			});
})

function changeToEditable(element){
	   var tdColl = $(element).parent().parent().find("td.editable");
		$.each(tdColl,function(index,obj){
			var type= $(obj).attr("type");
			var text = $(obj).text().trim();
	       if(type=="text"){
	    	   var input = $("<input style='width:100%' type='text'value='" + text + "'/>");
	    	   $(obj).html(input);
	       }else if(type=="select"){
	    	   var optionArray  = $(obj).attr("options").split("&");
	    	   var optionValueArray  = $(obj).attr("values").split("&");
	    	   var selectStr = '<select>';

	    	   $.each(optionArray,function(index,option){
	               var selected = text==optionArray[index].trim()?'selected':'';
	    		   selectStr += "<option value='" + optionValueArray[index].trim()  + "'"+ selected +">"+optionArray[index].trim()+"</option>"
	    	   })
	    	   selectStr += '</select>';
	    	   var input = $(selectStr);
	    	   $(obj).html(input);

	       }

		})
		$(element).removeClass("editoper");
		$(element).addClass("canceledit");
		$(element).next().removeClass("disabled");
		$(element).val('取消');
}

   function changeToCancel(element,operation){
	   var tdColl = $(element).parent().parent().find("td.editable");
		$.each(tdColl,function(index,obj){
			var type= $(obj).attr("type");
			if(operation=="cancel"){
			   $(obj).html($(obj).attr("orign"));
			}else{//保存
				if(type=="text"){
					$(obj).html($(this).children(":first").val());
			    }else if(type=="select"){
			    	$(obj).html($(this).children(":first").find("option:selected").text());
			    }
			}

		})
		$(element).removeClass("canceledit");
		$(element).addClass("editoper");
		$(element).next().addClass("disabled");
		$(element).val('编辑');
   }

   //替换控件的orign的值
   function replaceOrign(element){
	   var tdColl = $(element).parent().parent().find("td.editable");
		$.each(tdColl,function(index,obj){
			var type= $(obj).attr("type");
			$(obj).attr("orign",$(obj).text().trim());
		})
   }

function hidePlaceHolder(element){
	$(element).css('visibility','hidden');
	$(element).css('height','0');
	$(element).css('padding','0');
}

function showPlaceHolder(element){
	$(element).css('visibility','visible');
	$(element).css('height','auto');
	$(element).css('padding','15px');
}

function showWaitMsg(msg) {
	if (msg) {

	} else {
		msg = '正在处理，请稍候...';
	}
	var panelContainer = $("body");
	$("<div id='msg-background' class='datagrid-mask' style=\"display:block;z-index:10006;\"></div>").appendTo(panelContainer);
	var msgDiv = $("<div id='msg-board' class='datagrid-mask-msg' style=\"display:block;z-index:10007;left:50%\"></div>").html(msg).appendTo(
			panelContainer);
	msgDiv.css("marginLeft", -msgDiv.outerWidth() / 2);
}
function hideWaitMsg() {
	$('.datagrid-mask').remove();
	$('.datagrid-mask-msg').remove();
}


/***
 * 提供给overview的加载处理数据接口
 * @param obj view的对象
 */
function overviewrender(obj){
	var model = obj.model.toJSON()
    , markUp = obj.template(model)
    , self = obj;

  //self.dataTable.removeRows(0,self.dataTable.getNumberOfRows())
  var xAxis_v=new Array();
  var current=new Array();
  var rangeArray =new Array();//记录有多少个同一个app的区间
  var rangeDot;
  var distance = 0;//记录同一个app名称下server遍历的间距
  var fromIndex = 0;//记录区域的范围下标
  var xplotBands = new Array();
//var ptStart=new Date();
  $.each(model.data, function(index, obj){
    var xdata = obj[1] + '-' + obj[2];
	xAxis_v.push(xdata);
	current.push(obj[0]);

	if(!rangeDot){
		rangeDot = obj;
	}

	if(rangeDot != obj && rangeDot[1] != obj[1]){
		if( distance > 1){
		var rangeItem = new Array();
		rangeItem.push(rangeDot);
		rangeItem.push(model.data[index-1]);
		rangeItem.push(fromIndex);
		rangeItem.push(index-1);
		rangeArray.push(rangeItem);
		}
		distance = 0;
		fromIndex = index;
		rangeDot = obj;
	}

	distance++;
  });

  $.each(rangeArray,function(index,obj){
		  var color = '#E0EEE0';
		  if((index%2)==0){
	         color='#E0FFFF';
	      }
	      xplotBands.push(
				  {
					 color:color,
					 from:obj[2],
					 to:obj[3],
					 label:{
					  text:obj[0][1]
				     }
				  }
		  )

  });

  var chart=self.chart.highcharts();
  chart.xAxis[0].update({
	categories: xAxis_v,
	plotBands:xplotBands
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