package com.webproject.pi.controllers.virtual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.pi4j.io.gpio.GpioController;
import com.webproject.pi.ic2.I2CProvider;

public class I2CPCA9685GpioProviderVirtual implements I2CProvider {
	final static Logger logger = LoggerFactory.getLogger(I2CPCA9685GpioProviderVirtual.class);

	@Override
	public void init(JsonNode node, GpioController gpio) throws Exception {
		logger.info("init");
	}

}
