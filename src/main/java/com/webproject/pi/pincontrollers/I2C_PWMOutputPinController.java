package com.webproject.pi.pincontrollers;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.webproject.pi.PiSystem;
import com.webproject.pi.ic2.I2CPCA9685GpioProvider;

public class I2C_PWMOutputPinController extends PinController{
	private Pin pwmPin;
	private PCA9685GpioProvider pcaGpio;
	
	@Override
	public void init(String pinName, PiSystem pi) throws Exception{
		String parts[] = pinName.split("\\.");
		String ic2Name = parts[0];
		
		I2CPCA9685GpioProvider prov = (I2CPCA9685GpioProvider)(pi.getI2CManager().getIC2Provider(ic2Name));
		pcaGpio = prov.getPCA9685Provider();
		pwmPin = prov.getPin(pinName);
	}

	@Override
	public void setValue(int value) {
		pcaGpio.setPwm(pwmPin, 500, value);
	}
}
