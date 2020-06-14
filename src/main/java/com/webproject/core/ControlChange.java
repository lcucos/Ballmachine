package com.webproject.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlChange {

	@JsonProperty("driver")
    private String driverID;

	@JsonProperty("relative")
    private Boolean bRel=false;

	@JsonProperty("value")
    private Float value;
	
	@JsonProperty("status")
    private int status = ControlValue.STATUS_UNDEFINED;

	public ControlChange() {
    }
    
    public ControlChange(String driverID, Boolean bRel, Float value, Integer stts){
    	this.driverID = driverID;
    	this.value = value;
    	
    	if(bRel==null){
    		bRel = false;
    	}
    	this.bRel = bRel;
    	
    	if(stts!=null){
    		this.status = stts;
    	}
    }
    
    public String toString() {
        return "Driver=" + driverID+ ", value="+value + ", " + (bRel?"relative" : "absolute"); 
    }
    
    public String getDriverID(){
    	return driverID;
    }

	public float getValue() {
		return value;
	}

	public boolean isRelative() {
		return bRel;
	}
	
	public double getRequestedAbsoluteValue(double val){
		return bRel?(val+value):value;
	}
	
	public int getStatus(){
		return status;
	}
}
