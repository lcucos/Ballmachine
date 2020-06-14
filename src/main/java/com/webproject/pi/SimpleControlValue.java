package com.webproject.pi;

public class SimpleControlValue{
	int control;
	double value;
	
	public SimpleControlValue(int c, double val) {
		control = c;
		value = val;
	}
	public SimpleControlValue(String vals[]) throws Exception {
		control = Integer.parseInt(vals[0].trim());
		value   = Double.parseDouble(vals[1].trim());
		if(value<0 || value > 10000) {
			throw new Exception("Invalid value" + value);
		}
		if(control<0 || control > 10000) {
			throw new Exception("Invalid value" + value);
		}
	}		
}