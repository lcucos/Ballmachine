define([
    "backbone"
], function (Backbone) {
   // Todo: add a proper model
    var Users = {}

    Users.Model = Backbone.Model.extend({
        // use a function to always return NEW objects
        defaults:function () {
            return {
                username:"",
                email:"",
                fullname:"",
                md5pass:"",
                role:"",
            }
        },
        urlRoot: '/users',
    })

    Users.Collection = Backbone.Collection.extend({
        model:Users.Model,
        url:"/users"
    })

    // always return the object that you export or else face undefined consequences
    return Users
})