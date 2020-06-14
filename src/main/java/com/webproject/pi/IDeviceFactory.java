package com.webproject.pi;

import com.webproject.pi.ic2.I2CProvider;

public interface IDeviceFactory {
	// Sensors Types
	public static final String  EXT_RPM_READER = "extRPMReader";
	
	// Drivers Types
	public static final String STEPPER_DRIVER = "STEPPER";
	public static final String MOTOR_DRIVER   = "MOTOR";

	// I2C Types
	public static final String I2C_PCA9685 = "PCA9685";
	
	public AbstractController createControler(String type);
	public AbstractSensorReader createSensor(String type);
	public I2CProvider createProvider(String type);
}
