
var BaseInfoWidget = BaseWidget.extend({

  initWidget : function() {

    this.Name = "Base Info Widget"
    this.init()
    this.updateFrequency = 10000 // every 10 seconds
    // templates
    var templateName = "#" + this.TemplateName;

    var templateSource        = $(templateName).html()
    this.template         = Handlebars.compile(templateSource)
    this.$el.empty().html(this.template())
}

, render: function() {
	var model = this.model.toJSON()
    , markUp = this.template(model.data)
    $(this.el).html(markUp)
}
})