/**
 * Created by wangwei-ww on 2016/1/29.
 */
$(".arrow").on("click",function (e){
	$.ajax({
		url: "/jmx0/getHosts",
		type: "POST",
		cache: false,
		data: {"appId" : $(this).attr("data-id")}, 
		success: function(data){
			alert(_.template($('#hosts').html(), data));
		},
		error: function(data){
			alert("err");
		}
	});
});