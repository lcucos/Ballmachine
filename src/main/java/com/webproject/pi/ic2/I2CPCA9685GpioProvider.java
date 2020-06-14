package com.webproject.pi.ic2;

import java.math.BigDecimal;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

public class I2CPCA9685GpioProvider implements I2CProvider{
	private PCA9685GpioProvider pcaGpio;
	private HashMap<String, Pin> pins = new HashMap<String, Pin> ();
	
	@Override
	public void init(JsonNode node, GpioController gpio) throws Exception {
		int bus   = node.get("bus").asInt();
		int address = (int)Long.parseLong(node.get("address").asText(), 16);
		String name = node.get("name").asText();
		String freq = node.get("frequency").asText();
		String freqCorrectionFactor = node.get("frequencyCorrectorFactor").asText();
		
		I2CBus i2c = I2CFactory.getInstance(bus);
		BigDecimal frequency = new BigDecimal(freq);
		BigDecimal frequencyCorrectionFactor = new BigDecimal(freqCorrectionFactor);

		pcaGpio = new PCA9685GpioProvider(i2c, address, frequency, frequencyCorrectionFactor);
		for(int i=0;i<PCA9685Pin.ALL.length;i++) {
			String pinName = name+".PWM_"+(i<10?"0":"")+i;
            gpio.provisionPwmOutputPin(pcaGpio, PCA9685Pin.ALL[i], pinName);
            pins.put(pinName, PCA9685Pin.ALL[i]);
		}
		
		pcaGpio.reset();
	}
	
	public PCA9685GpioProvider getPCA9685Provider() {
		return this.pcaGpio;
	}
	
	public Pin getPin(String pinName) {
		return this.pins.get(pinName);
	}
}