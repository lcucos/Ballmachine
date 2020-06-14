package com.webproject.pi.ic2;

import com.fasterxml.jackson.databind.JsonNode;
import com.pi4j.io.gpio.GpioController;

public interface I2CProvider{
	void init(JsonNode node, GpioController gpio) throws Exception;
}