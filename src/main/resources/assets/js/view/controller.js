
define([
    "underscore", "jquery", "backbone", "bootstrap", "bootbox", 
    "text!tpl/controller.html"
], function (_, $, Backbone, bootstrap, bootbox, ControllerHtml) {
    // not implemented yet
    //
    var ControllerView = Backbone.View.extend({

    initialize:function () {
        console.log('Initializing Controller View');
        this.$el.html(ControllerHtml);
        var that = this;
        setTimeout(function(){that.refreshState(true)},10);
    },

    events: {
        "click #upButton"     : "up",
        "click #downButton"   : "down",
        "click #leftButton"   : "left",
        "click #rightButton"  : "right",
        "click #sendtopSpeed" : "sendTopSpeed",
        "click #sendbotSpeed" : "sendBotSpeed",
        "click #sendUpDown"   : "sendUpDown",
        "click #sendLeftRight": "sendLeftRight",
        "click #feedStartStop": "feedStartStop",
        "click #sendFeedSpeed": "sendFeedSpeed",
    },
    endisableAll:function(isEnabled){
         var valStatus = $('#feedStartStop').val();
         var status = (valStatus==="Start"?1:0);
         
         if(isEnabled && status == 1){
         	$("#feedStartStop").removeAttr("disabled");
         }else{
         	$("#feedStartStop").attr("disabled", true);
         }
    },
	updateControls:function(data, updateAll){
    			if(data.error) {  // If there is an error, show the error messages
                    $('.alert-error').text(data.error.text).show();
                }
                else{ 
                    // set values
    		     	$("#topSpeedActual").val (data.state.topSpeed.actualValue);
    		     	$("#botSpeedActual").val (data.state.botSpeed.actualValue);
    		     	$("#leftrightAngle").val (data.state.horizStepper.actualValue);
    		     	$("#updownAngle").val (data.state.vertStepper.actualValue);
    		     	// set feedStartStop state
    		     	if(data.state.feeder.status == 0){
    		     		//console.log("Feeder is stoped");
    		     		$("#feedStartStop").val ("Start");
    		     		$("#feedStartStop").text ("Start");
    		     		$('#feedStartStop').css({backgroundColor: 'lightgreen'});
    		     	}else{
    		     	    //console.log("Feeder is started");
    		     		$("#feedStartStop").val ("Stop");
    		     		$("#feedStartStop").text ("Stop");
    		     		$('#feedStartStop').css({backgroundColor: 'pink'});
    		     	}
    		     	if(updateAll){
    		     		$("#feedSpeedRequested").val (data.state.feeder.req);
    		     		$("#topSpeedRequested").val (data.state.topSpeed.req);
    		     		$("#botSpeedRequested").val (data.state.botSpeed.req);
    		     		$("#updownAngleRequested").val (data.state.vertStepper.req);
    		     		$("#leftrightAngleRequested").val (data.state.horizStepper.req);
    		     	}
    		     }
    			//console.log('refresh : ' +JSON.stringify(data.state.topSpeed.actualValue) );	
	},
	refreshState:function (updateAll) {
	    that=this;
  		$.ajax({
    		url: 'controller/state',
    		type:'GET',
            dataType:"json",
            contentType: "application/json; charset=utf-8",
    		success: function(data) {
    			that.updateControls(data, updateAll);
    		},
    		error: function(XMLHttpRequest, textStatus, errorThrown){
    		     $('#feedStartStop').css({backgroundColor: 'lightgray'});
    		}
  		});
        var that = this;
        setTimeout(function(){that.refreshState(false)},2000);
	},
	
    render:function () {
        //$(this.el).html(this.template());
        // setTimeout(executeQuery, 2000);
        return this;
    },
    feedStartStop:function(event){
	    var valStr = $('#feedSpeedRequested').val();
	    var valStatus = $('#feedStartStop').val();
	    this.updown(event, valStr, "feeder", false, (valStatus==="Start"?1:0));    	    	
    },
    sendFeedSpeed:function(event){
	    var valStr = $('#feedSpeedRequested').val();
	    var valStatus = $('#feedStartStop').val();
	    this.updown(event, valStr, "feeder", false, (valStatus==="Start"?0:1));    	    	
    },
    sendUpDown:function(event){
	    var valStr = $('#updownAngleRequested').val();
	    this.updown(event, valStr, "vertStepper", false);    	
    },
    sendLeftRight:function(event){
	    var valStr = $('#leftrightAngleRequested').val();
	    this.updown(event, valStr, "horizStepper", false);    	
    },
    sendBotSpeed:function(event){
	    var valStr = $('#botSpeedRequested').val();
	    this.updown(event, valStr, "botSpeed", false);
	},
	sendTopSpeed:function(event){
	    var valStr = $('#topSpeedRequested').val();
	    this.updown(event, valStr, "topSpeed", false);
	},
    down:function (event) {
        var valStr = $('#selUpDown option:selected').text();
        var valReq = $('#updownAngle').val()*1;
        var value = valStr.replace(/\s.*/,"")*(-1);
        $("#updownAngleRequested").val (value + valReq);
		this.updown(event,  value, "vertStepper", true);
	},
    up:function (event) {
        var valStr = $('#selUpDown option:selected').text();
        var valReq = $('#updownAngle').val()*1;
        var value = valStr.replace(/\s.*/,"")*(1);
        $("#updownAngleRequested").val (value + valReq);
		this.updown(event,  value, "vertStepper", true);
    },
    left:function (event) {
        var valStr = $('#selLeftRight option:selected').text();
        var valReq = $('#leftrightAngle').val()*1;
        var value = valStr.replace(/\s.*/,"")*(1);
		$("#leftrightAngleRequested").val (value + valReq);
		this.updown(event,  value, "horizStepper", true);
	},
    right:function (event) {
        var valStr = $('#selLeftRight option:selected').text();
        var valReq = $('#leftrightAngle').val()*1;
        var value = valStr.replace(/\s.*/,"")*(-1);
		$("#leftrightAngleRequested").val (value + valReq);
		this.updown(event,  value, "horizStepper", true);
    },
    
    updown:function(event, valStr, driverStr, bRel, stts){
        event.preventDefault(); // Don't let this button submit the form
        $('.alert-error').hide(); // Hide any errors on a new submit
        var url = 'controller';
        
        var formValues = {
            driver : driverStr,
            relative : bRel,
            value: valStr,
            status: stts
        };
        console.log('Change : ' + driverStr+' : ' +JSON.stringify(formValues) );
		that = this;
        $.ajax({
            url:url,
            type:'POST',
            dataType:"json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(formValues) ,
            success:function (data) {
                console.log(["Data change request: ", data]);
                that.updateControls(data, true);
                /*
                if(data.error) {  // If there is an error, show the error messages
                    $('.alert-error').text(data.error.text).show();
                }
                else { // If not, send them back to the home page
                    window.location.replace('#');
                }
                */
            }
        });
    }
    })
    return ControllerView;
});

