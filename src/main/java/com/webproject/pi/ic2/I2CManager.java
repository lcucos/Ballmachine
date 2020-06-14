package com.webproject.pi.ic2;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pi4j.io.gpio.GpioController;
import com.webproject.pi.IDeviceFactory;

public class I2CManager {
	final static Logger logger = LoggerFactory.getLogger(I2CManager.class);

	private HashMap<String, I2CProvider> ic2ProvidersMap = new HashMap<String, I2CProvider>();
	
	public I2CProvider getIC2Provider(String name) {
		return ic2ProvidersMap.get(name);
	}
	
	public void init(JsonNode rootNode, GpioController gpio, IDeviceFactory factory) throws Exception {
		ArrayNode arrI2C = (ArrayNode)rootNode.get("i2c");
		if(arrI2C == null){
			return;
		}
		
		for(int i=0;i<arrI2C.size();i++) {
			JsonNode node = arrI2C.get(i);
			String name = node.get("name").asText();
			String type = node.get("type").asText();
			I2CProvider provider = factory.createProvider(type);
			if(provider==null) {
				throw new Exception("Unable to create I2c provider type: " + type);
			}
			provider.init(node, gpio);
			ic2ProvidersMap.put(name, provider);
		}
	}

}
