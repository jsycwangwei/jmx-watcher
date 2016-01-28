
var AppSettingWidget = BaseNoFreshWidget.extend({

	events : {
    "click .saveparam" : "saveparam"
        },
  initialize : function() {

    this.Name = "AppSetting Widget";
    var appitem = $("#application").find("option:selected").attr("value");
	var params = {app:appitem};
    this.load(params);

    // templates
    var templateSource        = $("#appsetting-widget-template").html()
    this.template         = Handlebars.compile(templateSource)
    this.$el.empty().html(this.template())

  }

, render: function() {

	var model         = this.model.toJSON()
    , markUp        = this.template(model.data)

  $(this.el).html(markUp)
  this.initTab();


}, initTab: function(){

		   $('#appSettingTab a:first').tab('show');//初始化显示哪个tab
		   $('#appSettingTab a').click(function(e){
		     e.preventDefault();//阻止a链接的跳转行为
			 $(this).tab('show');//显示当前选中的链接及关联的content
		   })

}, saveparam : function(e){
	var target = $(e).attr("currentTarget")
   var paramname = $(target).attr("paramName");
	var paramkey = $(target).attr("paramKey");
	var paramType = $(target).attr("paramType");
	var appid = $(target).attr("appid");
   var value = $("#" + paramname + "-" + paramkey).val();
   if(value==''|| value.length == 0){
	   alert("值不能为空");
	   return;
   }
   $.ajax({
			url:'/event/params/saveParam',
			dataType:'json',
			type:"POST",
			asyn:false,
			data:{appid:appid,paramname:paramname,paramtype:paramType,paramkey:paramkey,value:value},
            success:function(data){
				if(data.success){
                   $(target).next().attr('style','block');
                   $(target).next().removeClass();
                   $(target).next().addClass('ok');
				}
				else{
					$(target).next().removeClass();
				    $(target).next().addClass('error');
                    alert(data.msg);
				}
			},
			error:function(msg){
				alert("请求有异常");
			}
			});

}


})