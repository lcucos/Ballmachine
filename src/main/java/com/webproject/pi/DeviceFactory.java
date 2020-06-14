package com.webproject.pi;

import com.webproject.pi.ic2.I2CPCA9685GpioProvider;
import com.webproject.pi.ic2.I2CProvider;

public class DeviceFactory implements IDeviceFactory{

	public AbstractController createControler(String type){
		if(type.equals(STEPPER_DRIVER)){
			return new StepperController();
		}else if(type.equals(MOTOR_DRIVER)){
			return new MotorController();
		}
		return null;
	}

	@Override
	public AbstractSensorReader createSensor(String type) {
		if(type.equals(EXT_RPM_READER)) {
			return new ExternalGPIOValueReader();
		}
		return null;
	}

	@Override
	public I2CProvider createProvider(String type) {
		if(type.equals(I2C_PCA9685)) {
			return new I2CPCA9685GpioProvider();
		}
		return null;
	}	
}
