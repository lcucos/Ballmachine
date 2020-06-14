package com.webproject.pi.controllers.virtual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webproject.pi.AbstractSensorReader;

public class ExternalSensorReaderVirtual extends AbstractSensorReader{
	final static Logger logger = LoggerFactory.getLogger(ExternalSensorReaderVirtual.class);
	@Override
	public double getValue(String name) {
		return 0;
	}

}
