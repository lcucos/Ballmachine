package com.webproject.pi.controllers.virtual;

import com.webproject.pi.AbstractController;
import com.webproject.pi.AbstractSensorReader;
import com.webproject.pi.ExternalGPIOValueReader;
import com.webproject.pi.IDeviceFactory;
import com.webproject.pi.ic2.I2CPCA9685GpioProvider;
import com.webproject.pi.ic2.I2CProvider;

public class VirtualDeviceFactory implements IDeviceFactory {

	public AbstractController createControler(String type){
		if(type.equals(STEPPER_DRIVER)){
			return new StepperControllerVirtual();
		}else if(type.equals(MOTOR_DRIVER)){
			return new MotorControllerVirtual();
		}
		return null;
	}

	@Override
	public AbstractSensorReader createSensor(String type) {
		if(type.equals(EXT_RPM_READER)) {
			return new ExternalSensorReaderVirtual();
		}
		return null;
	}

	@Override
	public I2CProvider createProvider(String type) {
		if(type.equals(I2C_PCA9685)) {
			return new I2CPCA9685GpioProviderVirtual();
		}
		return null;
	}

}
