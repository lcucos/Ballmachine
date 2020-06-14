define([
    'underscore', 'jquery', 'backbone',
    "view/login",
    "view/controller",
    "view/main"
], function (_, $, Backbone, 
             LoginView,
             ControllerView,
             MainView) {

    // add close method to all views for clean-up
    Backbone.View.prototype.close = function () {
        // call user defined close method if exists
        if (this.beforeClose) {
            this.beforeClose()
        }
        //for (var index in this._periodicFunctions) {
        //    clearInterval(this._periodicFunctions[index])
        //}
        this.remove()
        this.unbind()
    }

    /**
     * Use callPeriodically to register functions that poll the server periodically and update the view.
     * They will automatically stop when the view close method is invoked.
     */
     /*
    Backbone.View.prototype.callPeriodically = function (callback, interval) {
        if (!this._periodicFunctions) {
            this._periodicFunctions = []

        }
       this._periodicFunctions.push(setInterval(callback, interval))
    }
   */
    var AppRouter = Backbone.Router.extend({
        routes:{
            "login" : "login",
            "controller" : "controller",
            "main" : "main",
            "*path":"defaultRoute"
        },
        initialize:function(){
        
        	console.log("Init router");
        	 $.ajaxSetup({
        	   statusCode: {
        			401: function(){
            			// Redirec the to the login page.
            			console.log("Redirect to login");
            			window.location.replace('/#login');
		            },
        			403: function(){
            			// Redirec the to the login page.
            			window.location.replace('/#login');
		            },
		       }
        	 });
        	 
        },
        showView:function (selector, view) {
            // close the previous view - does binding clean-up and avoids memory leaks
            if (this.currentView) this.currentView.close()
            // render the view inside the selector element
            $(selector).html(view.render().el)
            this.currentView = view   
            return view
        },
        login: function() {
           this.showView("#container", new LoginView());
        },
        controller: function(){
           this.showView("#container", new ControllerView());
        },
        main:function(){
        	this.showView("#container", new MainView());
        },
        defaultRoute:function () {
            this.navigate("#controller")
        },
        userslist:function () {
            console.log("show users");
            var that = this,
                users = new Users.Collection
            // fetch the collection and update pass it to the view on success
            users.fetch({success:function () {
                that.showView("#container", new UsersListView({
                    collection:users
                }))
            }})
        },
        showUser:function (cid) {
            var that = this;
            var user = new Users.Model({id: cid});
            user.fetch({
   				success: function (user) {
                   that.showView("#container", new UserForm({
                      model:user
                   }))
    			}
    			
			});               
        },
        newUser:function (id) {
            var user = new Users.Model();
            this.showView("#container", new UserForm({
                      model:user
                   }));        
        },
        //Submits an invalid authentication header, causing the user to be 'logged out'
		logoutFake:function() {
		    window.sessionStorage.clear();
			console.log("logout");
    		$.ajax({
        		type: "GET",
    			url: "/sites",
    			dataType: 'json',
    			async: true,
    			username: "some_username_that_doesn't_exist",
    			password: "any_stupid_password",
    			data: '{ "comment" }'
			})
			//In our case, we WANT to get access denied, so a success would be a failure.
			.done(function(){
    			alert('Error!')
			})
			//Likewise, a failure *usually* means we succeeded.
			//set window.location to redirect the user to wherever you want them to go
			.fail(function(){
    			window.location = "/";
    		});
		}  
    })

    return AppRouter
})
