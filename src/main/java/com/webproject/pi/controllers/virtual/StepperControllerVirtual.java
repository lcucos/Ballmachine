package com.webproject.pi.controllers.virtual;

import com.fasterxml.jackson.databind.JsonNode;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.webproject.core.ControlChange;
import com.webproject.core.ControlValue;
import com.webproject.pi.PiSystem;
import com.webproject.pi.StepperController;

public class StepperControllerVirtual extends StepperController{
	ChangeValueSimulator val = new ChangeValueSimulator();

	public StepperControllerVirtual() {
	}
	
	public void allocatePins(PiSystem pi, JsonNode pins) throws Exception {
	}
	
	public void setValuesInternal(ControlChange obj) throws InterruptedException {
		angle.setRequestValue(obj.getValue());
		angle.setStatus(obj.getStatus());
		val.changeValue(angle, obj.getRequestedAbsoluteValue(angle.getActualValue()),10);
	}
	public ControlValue getValue(String name) {
		return angle;
	}
}
