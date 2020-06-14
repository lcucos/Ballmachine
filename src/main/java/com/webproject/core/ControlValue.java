package com.webproject.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlValue {
	public static final int TOLERANCE_ABSOLUTE = 1;
	public static final int TOLERANCE_PERCENT  = 1;
	
	public static final int STATUS_UNDEFINED = -1;
	public static final int STATUS_ON  = 1;  
	public static final int STATUS_OFF = 0;
	
	@JsonProperty("name")
	String name;
	
	@JsonProperty("act")
	double actual;	

	@JsonProperty("req")
	double requested = -999;
	
	@JsonProperty("reqT")
	long requestTime = 0;
	
	@JsonProperty("status")
	int status = STATUS_UNDEFINED;

	int     toleranceType  = TOLERANCE_ABSOLUTE; 
	double  precision = 1;
	
	
	public ControlValue(String name, double tolerance){
		this.name = name;
		this.precision = 1.0/tolerance;
	}

	public void setStatus(int val){
		status = val;
	}

	public int getStatus(){
		return status ;
	}

	public void setRequestValue(double val){
		requested = val;
		requestTime = System.currentTimeMillis();
	}
	
	public void setActualValue(double val){
		actual = val;
	}
	
	private double getValueWithTolerance(double value){
		 return Math.floor(Math.round(value*precision)) / precision;
	}

	@JsonProperty("match")
	public double getMatchedPercent() {
		if(requested==0){
			return 0;
		}
		return (1-(requested - actual)/requested)*100;
	}
	
	public double getActualValue(){
		return getValueWithTolerance(actual);
	}
	
	public static void main(String args[]){
		ControlValue cv = new ControlValue("", 0.1);
		double val = cv.getValueWithTolerance(100.02);
		System.out.println(val);
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name + "["+status+"]:" + getActualValue();  
	}
}
