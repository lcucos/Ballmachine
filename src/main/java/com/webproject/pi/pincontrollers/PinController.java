package com.webproject.pi.pincontrollers;

import com.webproject.pi.PiSystem;

public abstract class PinController {
	public abstract void init(String pinName, PiSystem pi) throws Exception;
	public abstract void setValue(int value);
}
