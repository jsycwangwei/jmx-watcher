var HandlerbarHelper = {

	    RegisterPartials : function(){

	        Handlebars.registerPartial("date-dropdown", $("#date-dropdown-template").html());

	   },
	   RegisterHelpers : function(){

		    Handlebars.registerHelper('hash', function ( context, options ) {

		              var ret = ""
		                , counter = 0
                     if(context){
		              $.each(context, function ( key, value ) {
		                if (typeof value != "object") {
		                  obj = { "key" : key, "value" : value , "index" : counter++ }
		                  ret = ret + options.fn(obj)
		                }

		              })
                     }

		              return ret
		    });

		  Handlebars.registerHelper('addOne', function ( index ) {

              return index + 1;
          })
          Handlebars.registerHelper('firstElemShow', function ( index ) {
              if(index!=0){
                return "style=display:none";
              }
              return "";
          })
          Handlebars.registerHelper('firstElemColor', function ( index ) {
              if(index!=0){
                return "";
              }
              return "style=background-color:rgb(124,197,229)";
          })
          Handlebars.registerHelper('getParamUnit', function (paramName) {
        	  var unit = '';
              if(paramName=='thread'){
            	  unit='个';
              }else if(paramName=='gc'){
            	  unit='B';
              }else if(paramName=='memory'){
            	  unit='B';
              }else if(paramName=='database'){
            	  unit='个';
              }

              return unit;
          })
		  }

	  }