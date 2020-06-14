package com.webproject.pi.pincontrollers;

public class PinControllerFactory {
	private PinControllerFactory() {}
	
	
	private static PinControllerFactory _instance = new PinControllerFactory();
	
	public static PinControllerFactory getInstance() {
		return _instance;
	}
	
	public PinController getPWMOutputPinController(String pinName) {
		if(pinName.contains("GPIO")) {
			return new GPIO_PWMOutputPinController();
		}else if(pinName.contains(".PWM")) {
			return new I2C_PWMOutputPinController();			
		}
		return null;
	}
}
