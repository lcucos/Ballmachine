package com.webproject.core;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemState {
	
	@JsonProperty("state")	
	HashMap<String, ControlValue> vals= new HashMap<String, ControlValue>();
	
	public void addControlValue(String id, ControlValue val){
		vals.put(id, val);
	}
	
	public String toString() {
		StringBuilder stBuilder = new StringBuilder();
		for(String key:vals.keySet()) {
			stBuilder.append(key);
			stBuilder.append(":");
			stBuilder.append(vals.get(key));
			stBuilder.append(", ");
		}
		return stBuilder.toString();
	}
}
