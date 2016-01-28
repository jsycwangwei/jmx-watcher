
var ServerSettingWidget = BaseNoFreshWidget.extend({

  initialize : function() {

    this.Name = "ServerSetting Widget"
    var selectedproject = $("#selectedProject").val();
	var params = {project:selectedproject};
    this.load(params);

    // templates
    var templateSource        = $("#serversetting-widget-template").html()
    this.template         = Handlebars.compile(templateSource)
    this.$el.empty().html(this.template())

  }

, render: function() {

	var model         = this.model.toJSON()
    , markUp        = this.template(model.data)

  $(this.el).html(markUp)
}

})