package com.webproject.pi;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class PinMapper {
	private HashMap<String, Pin> rawBroadComMap = new HashMap<String, Pin>();

	private PinMapper(){
		// prepare BroadCom pin text name to GPIO object map
		rawBroadComMap.put("GPIO_02", RaspiPin.GPIO_08);
		rawBroadComMap.put("GPIO_03", RaspiPin.GPIO_09);
		rawBroadComMap.put("GPIO_04", RaspiPin.GPIO_07);
		rawBroadComMap.put("GPIO_17", RaspiPin.GPIO_00);
		rawBroadComMap.put("GPIO_27", RaspiPin.GPIO_02);
		rawBroadComMap.put("GPIO_22", RaspiPin.GPIO_03);
		rawBroadComMap.put("GPIO_10", RaspiPin.GPIO_12);
		rawBroadComMap.put("GPIO_09", RaspiPin.GPIO_13);
		rawBroadComMap.put("GPIO_11", RaspiPin.GPIO_14);
		rawBroadComMap.put("GPIO_05", RaspiPin.GPIO_21);
		rawBroadComMap.put("GPIO_06", RaspiPin.GPIO_22);
		rawBroadComMap.put("GPIO_13", RaspiPin.GPIO_23);
		rawBroadComMap.put("GPIO_19", RaspiPin.GPIO_24);
		rawBroadComMap.put("GPIO_26", RaspiPin.GPIO_25);
		rawBroadComMap.put("GPIO_14", RaspiPin.GPIO_15);
		rawBroadComMap.put("GPIO_15", RaspiPin.GPIO_16);
		rawBroadComMap.put("GPIO_18", RaspiPin.GPIO_01);
		rawBroadComMap.put("GPIO_23", RaspiPin.GPIO_04);
		rawBroadComMap.put("GPIO_24", RaspiPin.GPIO_05);
		rawBroadComMap.put("GPIO_25", RaspiPin.GPIO_06);
		rawBroadComMap.put("GPIO_08", RaspiPin.GPIO_10);
		rawBroadComMap.put("GPIO_07", RaspiPin.GPIO_11);
		rawBroadComMap.put("GPIO_12", RaspiPin.GPIO_26);
		rawBroadComMap.put("GPIO_16", RaspiPin.GPIO_27);
		rawBroadComMap.put("GPIO_20", RaspiPin.GPIO_28);
		rawBroadComMap.put("GPIO_21", RaspiPin.GPIO_29);//26 pins
	}
	
	private static PinMapper _instance = new PinMapper();
	public static PinMapper getInstance() {return _instance;}
	
	public Pin getBCommPinFromGPIO(String str) throws Exception{
		Pin retPin = rawBroadComMap.get(str);
		if(retPin==null) {
			throw new Exception("Unknown GPIO pin: " + str);
		}
		return retPin;
	}

	public Pin[] getBCommPinFromGPIOs(ArrayNode arrayNode) throws Exception {
		Pin pins[] = new Pin[arrayNode.size()];
		for(int i=0;i<arrayNode.size();i++) {
			pins[i] = this.getBCommPinFromGPIO(arrayNode.get(i).asText());
		}
		return pins;
	}
}
