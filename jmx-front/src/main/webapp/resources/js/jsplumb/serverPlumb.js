
jsPlumb.ready(function() {
	   ServerSettingApp.init();//将动态界面在此初始化加载
         var instance = jsPlumb.getInstance({
                   // default drag options
                   DragOptions : { cursor: 'pointer', zIndex:2000 },
                   // the overlays to decorate each connection with.  note that the label overlay uses a function to generate the label text; in this
                   // case it returns the 'labelText' member that we set on each connection in the 'init' method below.
                   ConnectionOverlays : [
                            [ "Arrow", { location:1 } ],
                            [ "Label", {
                                     location:0.1,
                                     id:"label",
                                     cssClass:"aLabel"
                            }]
                   ],
                   Container:"setting-container"
         });

         // this is the paint style for the connecting lines..
         var connectorPaintStyle = {
                   lineWidth:2,
                   strokeStyle:"#61B7CF",
                   joinstyle:"round",
                   outlineColor:"white",
                   outlineWidth:2
         },
         // .. and this is the hover style.
         connectorHoverStyle = {
                   lineWidth:4,
                   strokeStyle:"#216477",
                   outlineWidth:2,
                   outlineColor:"white"
         },
         endpointHoverStyle = {
                   fillStyle:"#216477",
                   strokeStyle:"#216477"
         },
         // the definition of source endpoints (the small blue ones)
         sourceEndpoint = {
                   endpoint:"Dot",
                   paintStyle:{
                            strokeStyle:"#7AB02C",
                            fillStyle:"transparent",
                            radius:1,
                            lineWidth:2
                   },
                   isSource:true,
                   connector:[ "Flowchart", { stub:[40, 60], gap:10, cornerRadius:1, alwaysRespectStubs:true } ],
                   connectorStyle:connectorPaintStyle,
                   hoverPaintStyle:endpointHoverStyle,
                   connectorHoverStyle:connectorHoverStyle,
        dragOptions:{}

         },
         // the definition of target endpoints (will appear when the user drags a connection)
         targetEndpoint = {
                   endpoint:"Dot",
                   paintStyle:{ fillStyle:"#7AB02C",radius:1 },
                   hoverPaintStyle:endpointHoverStyle,
                   maxConnections:-1,
                   dropOptions:{ hoverClass:"hover", activeClass:"active" },
                   isTarget:true
         },
         init = function(connection) {
                   connection.getOverlay("label").setLabel(connection.sourceId.substring(15) + "-" + connection.targetId.substring(15));
                   connection.bind("editCompleted", function(o) {
                            if (typeof console != "undefined")
                                     console.log("connection edited. path is now ", o.path);
                   });
         };

         var _addEndpoints = function(toId, sourceAnchors, targetAnchors) {
                            for (var i = 0; i < sourceAnchors.length; i++) {
                                     var sourceUUID = toId + sourceAnchors[i];
                                     instance.addEndpoint(toId, sourceEndpoint, { anchor:sourceAnchors[i], uuid:sourceUUID });
                            }
                            for (var j = 0; j < targetAnchors.length; j++) {
                                     var targetUUID = toId + targetAnchors[j];
                                     instance.addEndpoint(toId, targetEndpoint, { anchor:targetAnchors[j], uuid:targetUUID });
                            }
                   };



         $(".project").on("click",function(){
             var proj = $(this).attr("value");
             var params = {project:proj};
             $(".project").removeClass("group-active");
             $(this).addClass("group-active");
             instance.detachEveryConnection();
             instance.deleteEveryEndpoint();
             ServerSettingApp.serverSettingWidget.LoadModel(params);
          })

          $("#serversetting-widget-placeholder").delegate('.application',"click",function(){
             var appid = $(this).attr("id");
             $(".application").removeClass("group-active");
             $(".server").removeClass("group-active");
             $(this).addClass("group-active");
             instance.detachEveryConnection();
             instance.deleteEveryEndpoint();

             $("div[id^='"+appid+"-s']").each(function(){
            	 var serverid = $(this).attr("id");
                 _addEndpoints(appid,["LeftMiddle"], ["RightMiddle"]);
                 _addEndpoints(serverid, ["LeftMiddle"], ["RightMiddle"]);
                 instance.connect({uuids:[appid + "RightMiddle", serverid + "LeftMiddle"], editable:true});
                 if($(this).attr('monitorstatus') == "true"){
                    $(this).addClass("group-active");
                 }
             })
          })
          $("#serversetting-widget-placeholder").delegate('.add-monitor',"click",function(){
             var targetid = $(this).attr("value");
             var server = targetid.split("-")[1].substring(1);
             var app = targetid.split("-")[0].substring(3);
             var addbtn = $(this);
//             _addEndpoints(appid,["LeftMiddle"], ["RightMiddle"]);
//             _addEndpoints(targetid, ["LeftMiddle"], ["RightMiddle"]);
//             instance.connect({uuids:[appid + "RightMiddle", targetid + "LeftMiddle"], editable:true});
             //doOther
             $.ajax({
 				url:'/event/monitor/addServer',
 				dataType:'json',
 				type:"POST",
 				asyn:false,
 				data:{server:server,app:app},
 				success:function(data){
	 				if(data.success){//表示成功
	 					addbtn.removeClass("add-monitor");
	 					addbtn.addClass("cancel-monitor");
	 					$("#"+targetid).attr('monitorstatus','true');
	 					if(instance.getEndpoints(targetid)){
	 		              $("#"+targetid).addClass("group-active");
	 					}
	 		            addbtn.html("<i class='fa-minus-circle'></i>取消监控");
	 				}else{
	 					alert(data.msg);
	 				}
 				},
 				error:function(msg){
 					alert("请求有异常");
 				}
 				});

          })

          $("#serversetting-widget-placeholder").delegate('.cancel-monitor',"click",function(){
        	  var targetid = $(this).attr("value");
        	  var server = targetid.split("-")[1].substring(1);
        	  var app = targetid.split("-")[0].substring(3);
        	  var cancelbtn = $(this);
//              instance.deleteEndpoint(targetid+"RightMiddle");
//              instance.deleteEndpoint(targetid+"LeftMiddle");
//              instance.deleteEndpoint(appid+"RightMiddle");
//              instance.deleteEndpoint(appid+"LeftMiddle");
              $.ajax({
   				url:'/event/monitor/cancelServer',
   				dataType:'json',
   				type:"POST",
   				asyn:false,
   				data:{server:server,app:app},
   				success:function(data){
	 				if(data.success){//表示成功
  	 					cancelbtn.removeClass("cancel-monitor");
  	 					cancelbtn.addClass("add-monitor");
  	 					$("#" + targetid).attr('monitorstatus','false')
  	 					if(instance.getEndpoints(targetid)){
  	 	                  $("#"+targetid).removeClass("group-active");
  	 					}
  	 	              cancelbtn.html("<i class='fa-plus-circle'></i>添加监控");
  	 				}else{
  	 					alert(data.msg);
  	 				}
   				},
 				error:function(msg){
 					alert("请求有异常");
 				}
   				});


          })
});