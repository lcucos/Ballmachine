package com.webproject.pi.controllers.virtual;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.webproject.core.ControlChange;
import com.webproject.core.ControlValue;
import com.webproject.pi.MotorController;
import com.webproject.pi.MotorController_to_DELETE;
import com.webproject.pi.PiSystem;
import com.webproject.pi.SimpleControlValue;

public class MotorControllerVirtual extends MotorController{
	
	final static Logger logger = LoggerFactory.getLogger(MotorControllerVirtual.class);

	private ChangeValueSimulator simulator = new ChangeValueSimulator();
	
	public MotorControllerVirtual() {
	}
	
	public void allocatePins(PiSystem pi, JsonNode pins) throws Exception {
		
	}
	@Override
	public void setValuesInternal(ControlChange obj) throws InterruptedException {
		speed.setRequestValue(obj.getValue());
		speed.setStatus(obj.getStatus());
		simulator.changeValue(speed, obj.getRequestedAbsoluteValue(speed.getActualValue()), 6);
	}
	
	public int calibrate() throws Exception {
		logger.info(this.name + ": Begin Calibration: START_CONTROL" + START_CONTROL + ", MAX_CONTROL="+MAX_CONTROL + ", steps="+this.calibration_steps);
		ArrayList<SimpleControlValue> arrOut = new ArrayList<SimpleControlValue>();
		
		for(int i=0;i<=this.calibration_steps;i++) {
			int cont = (MAX_CONTROL-START_CONTROL)/calibration_steps * i + START_CONTROL;
			System.out.println(this.name + ": set control = " +cont);
			simulator.changeValue(speed, cont*10, 3);
			
			Thread.sleep(3000);//sleep for 3 seconds to reach a stable speed
			SimpleControlValue cvp= new SimpleControlValue(cont, speed.getActualValue());
			arrOut.add(cvp);
		}
		valueControlMap.update(arrOut);
		logger.info(this.name + ": End Calibration");
		return 0;
	}
	public ControlValue getValue(String name) {
		return speed;
	}
}
