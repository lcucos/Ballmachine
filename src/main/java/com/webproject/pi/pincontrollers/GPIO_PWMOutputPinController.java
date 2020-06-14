package com.webproject.pi.pincontrollers;

import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.webproject.pi.PiSystem;
import com.webproject.pi.PinMapper;

public class GPIO_PWMOutputPinController extends PinController {
	private   GpioPinPwmOutput control;

	@Override
	public void init(String pinName, PiSystem pi) throws Exception {
		control = pi.getGpioController().provisionPwmOutputPin(PinMapper.getInstance().getBCommPinFromGPIO(pinName), 0);
        control.clearProperties();
        control.setPwmRange(1024);		
	}

	@Override
	public void setValue(int value) {
		control.setPwm(value);
	}
}
