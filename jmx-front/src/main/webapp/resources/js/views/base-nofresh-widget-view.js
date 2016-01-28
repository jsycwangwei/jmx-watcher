var BaseNoFreshWidget = Backbone.View.extend({

  Name : "BaseNoFreshWidget"

, load : function (params) {
	  this.params = params;
      this.pagination = false;//是否需要分页
      this.LoadModel(params)
      // set event listners
      this.model
        .on("error", this.error, this)
        .on("change", this.ModelChanged, this)

  }

, LoadModel : function (params) {
	this.params = params;
    this.model.fetch({
        data : params
    })

  }
,render: function() {

	var model         = this.model.toJSON()
    , markUp        = this.template(model.data)

  $(this.el).html(markUp)
  if(this.pagination){//如果需要分页
     this.pager(model.data.pager);
  }
  this.afterRender();
}
,pager : function(pager){
	var currentPage = pager.currentPage;
	var totalPages = pager.totalPages;
	var numberOfPages = 5;
	if(totalPages > 0){
	var pageArray = new Array();
	var self = this;
	var options = {
		    currentPage: currentPage,
		    totalPages: totalPages,
		    numberOfPages:numberOfPages,
		    itemTexts: function (type, page, current) {
		            switch (type) {
		            case "first":
		                return "首页";
		            case "prev":
		                return "&laquo;";
		            case "next":
		                return "&raquo;";
		            case "last":
		                return "尾页";
		            case "page":
		            	return page;

		            }
		        },
		    onPageClicked:function(event,originalEvent,type,page){
		        	switch (type) {
		            case "first":
		                self.params.currentpage = 1;
		            case "prev":
		            	self.params.currentpage = currentPage - 1;
		            case "next":
		            	self.params.currentpage = currentPage + 1;
		            case "last":
		            	self.params.currentpage = totalPages;
		            case "page":
		            	self.params.currentpage = page;

		        }

		        self.LoadModel(self.params);
		    }
	}

	   $('#' + this.paginationControl).bootstrapPaginator(options);
	}
}

, ModelChanged : function(){
     this.render()
},error : function(){

},afterRender: function(){

}


})