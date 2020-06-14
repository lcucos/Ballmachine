define([
    "underscore", "jquery", "backbone", 
], function (_, $, Backbone) {

	var SMBaseView = Backbone.View.extend({
		initialize:function () {
			console.log("base view");
		},
		getRole: function(){
		   	var role = window.sessionStorage.getItem("role");
		   	if(role === null){
		   	    // retrieve the role
		   	    console.log("retrieve permissions");
		   	    that = this
		   	    $.ajax({
 					 type: "GET",
  					 url: "users/info",
  					 async: false,   // temporary until change basic auth 
                     cache: false,
                     timeout: 10000,
					 success: function (results) {
					 	//console.log(JSON.stringify(results.permissions));
					 	window.sessionStorage.setItem("role", "retrieved");
					 	for(var i=0, len=results.length; i < len; i++){
					 		console.log("add permission : " + results[i]);
					 		window.sessionStorage.setItem(results[i], true);
					 	}
                     },
                     contentType: 'application/json',
					 dataType: 'json'
				});
        	}
			return role;			
		},
		getEnable: function(){
		    var role = this.getRole(); // wrong and stupid but quick for basic auth
		    if(window.sessionStorage.getItem(this.context)){
		    	return "";
		    }
			return "disabled";
		},
		getVisibility: function(){
		   var role = this.getRole(); // wrong and stupid but quick for basic auth
		   if(window.sessionStorage.getItem(this.context)){
		    	return "";
		   }
		   return "none";
		},
		/*
		getmap:function(catalog){
			var map = {};
			for(int i=0;i<catalog.length;i++){
			   map[catalog.id]=catalog.name;
			}
			return map;
		}
		*/
	});
	
  	return SMBaseView;
})