package com.webproject.pi;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class AbstractSensorReader {
	private String name;
	
	public void init(PiSystem piSystem, JsonNode node) {
		name = node.get("name").asText();
	}

	public String getName() {
		return name;
	}
	
	public abstract double getValue(String name);
}
