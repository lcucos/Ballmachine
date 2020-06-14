
define([
    "underscore", "jquery", "backbone", "bootstrap", "bootbox", 
    "text!tpl/main.html"
], function (_, $, Backbone, bootstrap, bootbox, MainHtml) {
    // not implemented yet
    //
    var MainView = Backbone.View.extend({

    initialize:function () {
        console.log('Initializing Main View');
        this.$el.html(MainHtml);
        var that = this;
        setTimeout(function(){that.refreshState(true)},10);
    },

    events: {
        "click #feedStartStop": "feedStartStop",
        "click #upFeed"       : "upFeed",
        "click #downFeed"     : "downFeed",
        "click #upSpeed"      : "upSpeed",
        "click #downSpeed"    : "downSpeed",
        "click #upSpin"       : "upSpin",
        "click #downSpin"     : "downSpin",        
        "click #upVAngle"     : "upVAngle",
        "click #downVAngle"   : "downVAngle",
        "click #upSweep"      : "upSweep",
        "click #downSweep"    : "downSweep",
    },
    endisableAll:function(isEnabled){
         var valStatus = $('#feedStartStop').val();
         var status = (valStatus.startsWith("Start")?1:0);
         
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
    		     	$("#feedValue").val(data.state.feeder.actualValue)
    		     	// set feedStartStop state
    		     	if(data.state.feeder.status == 0){
    		     		//console.log("Feeder is stoped");
    		     		$("#feedStartStop").val ("Start Feed");
    		     		$("#feedStartStop").text ("Start Feed");
    		     		$('#feedStartStop').css({backgroundColor: 'lightgreen'});
    		     	}else{
    		     	    //console.log("Feeder is started");
    		     		$("#feedStartStop").val ("Stop Feed");
    		     		$("#feedStartStop").text ("Stop Feed");
    		     		$('#feedStartStop').css({backgroundColor: 'pink'});
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
        return this;
    },
    feedStartStop:function(event){
	    var valStr = $('#feedValue').val();
	    var valStatus = $('#feedStartStop').val();
	    this.updown(event, valStr, "feeder", false, (valStatus.startsWith("Start")?1:0));    	    	
    },
    upFeed:function(event){
	    this.updown(event, 1, "feeder", true);    	    	
    },
    downFeed:function(event){
	    this.updown(event, -1, "feeder", true);    	
    },
    upVAngle:function(event){
	    var valStr = parseInt($('#updownAngle').val())+ 1;
	    this.updown(event, valStr, "vertStepper", false);    	
    },
    downVAngle:function(event){
	    var valStr = parseInt($('#updownAngle').val()) - 1;
	    this.updown(event, valStr, "vertStepper", false);    	
    },
    upSpeed:function(event){
	    var val = parseInt($('#topSpeedActual').val());
	    this.updown(event,  val+100, "topSpeed", false);
	},
    downSpeed:function(event){
	    var val = parseInt($('#topSpeedActual').val());
	    this.updown(event, val-100, "topSpeed", false);
	},
	upSpin:function(event){
	    var valStr = $('#spinValue').val();
	    this.updown(event, valStr + 10 , "spin", false);
	},
    downSpin:function (event) {
	    var valStr = $('#spinValue').val();
	    this.updown(event, valStr - 10 , "spin", false);
	},
    upSweep:function (event) {
	    var valStr = $('#leftrightAngle').val();
	    this.updown(event, valStr + 1 , "sweep", false);
    },    
    downSweep:function (event) {
	    var valStr = $('#leftrightAngle').val();
	    this.updown(event, valStr - 1 , "sweep", false);
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
            }
        });
    }
    })
    return MainView;
});

