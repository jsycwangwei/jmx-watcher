function format(date, tmpl){
			tmpl = tmpl || 'yyyy-MM-dd HH:mm:ss';
			
			var type = $.type(date);
			if(type === 'string'){
				date = +date;
			}
			
			if( type === 'number'){
				date = new Date(date);
			}
			
			var D = {
				yyyy: date.getFullYear(),
				MM: date.getMonth() + 1,
				dd: date.getDate(),
				HH: date.getHours(),
				mm: date.getMinutes(),
				ss: date.getSeconds()
			};
			
			for(var key in D){
				tmpl = tmpl.replace(new RegExp(key, 'g'), D[key]);
			}
			return tmpl;
		}
