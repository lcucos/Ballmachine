package com.webproject.pi;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.webproject.core.ControlChange;
import com.webproject.core.ControlValue;
import com.webproject.pi.pincontrollers.PinController;
import com.webproject.pi.pincontrollers.PinControllerFactory;

public class MotorController extends AbstractController{
	final static Logger logger = LoggerFactory.getLogger(MotorController.class);
	private AbstractSensorReader extReader = null;

	protected PinController control;
	protected ControlValue speed = new ControlValue("speed", 0.01);

	// callibration info: this needs to be externalized
	protected static final int MAX_CONTROL   = 1000;
	protected static final int MAX_VALUE     = 8500;
	
	protected static final int START_CONTROL = 0;
	protected int calibration_steps = 30;

	public MotorController() {
	}
	
	public void init(PiSystem piSystem, JsonNode node) throws Exception {
		// set sensor if available
		if(node.get("sensor")!=null) {
			extReader = piSystem.getSensor(node.get("sensor").asText());
			logger.info("set sensor reader: " + extReader.getName());
		}
		
		super.init(piSystem, node);
		logger.info("init MotorController: " + name);
	}
	
	public void allocatePins(PiSystem pi, JsonNode pins) throws Exception {
		String pinName = pins.get("control").asText();
		control = PinControllerFactory.getInstance().getPWMOutputPinController(pinName);
		control.init(pinName, pi);
	}

	@Override
	public void setValuesInternal(ControlChange obj) throws InterruptedException {
		speed.setRequestValue(obj.getValue());
		speed.setStatus(obj.getStatus());
		SimpleControlValue cv = this.valueControlMap.getControl(obj.getValue());

		if(cv!=null) {
			logger.info(getName() + ": request:" + obj.getValue() + ", control="+cv.control); 
			control.setValue(cv.control);
		}
	}

	@Override
	public ControlValue getValue(String name) {
		if(extReader!=null) {
			speed.setActualValue(extReader.getValue(this.name));
		}
		return speed;
	}
	
	public void setExtReader(ExternalGPIOValueReader reader){
		extReader = reader;
	}

	public int calibrate() throws Exception {
		if(extReader == null) {
			return -1; // no feedback reader
		}
		
		logger.info(this.name + ": Begin Calibration: START_CONTROL" + START_CONTROL + ", MAX_CONTROL="+MAX_CONTROL + ", steps="+this.calibration_steps);
		ArrayList<SimpleControlValue> arrOut = new ArrayList<SimpleControlValue>();
		
		for(int i=0;i<=this.calibration_steps;i++) {
			int cont = (MAX_CONTROL-START_CONTROL)/calibration_steps * i + START_CONTROL;
			logger.info(this.name + ": set control = " +cont);
			control.setValue(cont);
			Thread.sleep(15000);//sleep for 15 seconds to reach a stable speed
			SimpleControlValue cvp= new SimpleControlValue(cont, extReader.getValue(this.name));
			arrOut.add(cvp);
			// extra safety
			if(cvp.value > MAX_VALUE) {
				break;
			}
		}
		valueControlMap.update(arrOut);
		logger.info(this.name + ": End Calibration");
		return 0;
	}

}
