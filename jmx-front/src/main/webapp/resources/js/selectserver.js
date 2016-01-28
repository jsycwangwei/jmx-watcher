
			$("#appgroup").select2({width:150});
	   $("#application").select2({allowClear:true,width:150});
	   $("#appgroup").on("change",function(){
	      if($("#appgroup").find("option:selected").attr("value") != ''){
		     var project = $("#appgroup").find("option:selected").attr("value");
			 $.ajax({
				url:'/api/project/apps',
				dataType:'json',
				type:"POST",
				data:{project:project},
                complete:function(data,status){
				if($("#application").find("option:first").val() != ''){
					$("#application").html('');
					$("#application").select2({width:150});
				}else{
					$("#application").find("option").not(":first").remove();
					$("#application").select2({width:150});
				}
				$("#application").select2({
				width:150,
				data: eval("("+data.responseText+")")
				 });
				}
				});
		  }else{
		      $("#application").find("option").not(":first").remove();
              $("#application").select2({width:150});

		  }
		  });