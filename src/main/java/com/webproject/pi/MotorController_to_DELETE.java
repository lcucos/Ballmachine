package com.webproject.pi;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.webproject.core.ControlChange;
import com.webproject.core.ControlValue;
import com.webproject.pi.pincontrollers.PinController;
import com.webproject.pi.pincontrollers.PinControllerFactory;

public class MotorController_to_DELETE extends AbstractController implements GpioPinListenerDigital {
	private ExternalGPIOValueReader extReader;
	
	private   GpioPinDigitalInput sensor;
	private   PinController control;
	
	final static Logger logger = LoggerFactory.getLogger(MotorController_to_DELETE.class);
	
	private AtomicInteger counter = new AtomicInteger(0);
	private long lastTimestamp = 0;
	
	protected ControlValue speed = new ControlValue("speed", 0.01);
	private long lastVal = 0;
	
	protected static final int MAX_CONTROL   = 1000;
	protected static final int MAX_VALUE     = 6500;
	
	protected static final int START_CONTROL = 0;
	protected int calibration_steps = 30;
	
	public MotorController_to_DELETE() {
	}

	public void allocatePins(PiSystem pi, JsonNode pins) throws Exception {
		String pinName = pins.get("control").asText();
		control = PinControllerFactory.getInstance().getPWMOutputPinController(pinName);
		control.init(pinName, pi);
		
		/*
		control = pi.getGpioController().provisionPwmOutputPin(PinMapper.getInstance().getBCommPinFromGPIO(pinName), 0);
        control.clearProperties();
        control.setPwmRange(1024);
        */
        //extReader = PiSystem.getInstance().getExternalReader();
        /*
        if(arrPins.length > 1 && arrPins[1]!=null){
        	sensor = gpio.provisionDigitalInputPin(arrPins[1],        // PIN NUMBER
										"SpeedSensor" // PIN FRIENDLY NAME (optional)
        								,PinPullResistance.PULL_DOWN ); // PIN RESISTANCE (optional)
        	sensor.addListener(this);
        	sensor.setDebounce(10);
        	logger.info(" Register Listener for : " + name);
        	/ *
            Thread th = new Thread(new Runnable(){
    			public void run() {
    				try {
    					while(true){
    						Thread.sleep(1000);
    						updateSpeed();
    					}
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
            });
            th.start();
            * /

        }
	*/

	}
/*	
	public void allocatePins(GpioController gpio, Pin[] arrPins) {
        control = gpio.provisionPwmOutputPin(arrPins[0], 0) ;//.provisionDigitalOutputPin(arrPins[0], "Sp", PinState.LOW);
        extReader = PiSystem.getInstance().getExternalReader();
        control.setPwmRange(100);
	}
*/
		
	protected void updateSpeed() {
		long timestamp = System.currentTimeMillis();
		long crtVal = counter.get();
		long val = crtVal - lastVal ;
		lastVal = crtVal;
		double diff = (timestamp - lastTimestamp)/1000.0;
		speed.setActualValue((60 * val) / diff);
		logger.info("Reset counter : speed=" + speed.getActualValue()+ " counter=" + val);
		lastTimestamp = timestamp; 

	}

	public void handleGpioPinDigitalStateChangeEvent(
			GpioPinDigitalStateChangeEvent event) {
		
		if(event.getState()==PinState.LOW){
			return;
		}
		
		int val = counter.addAndGet(1);
		
		long timestamp = System.currentTimeMillis();
		double diff = (timestamp - lastTimestamp)/1000.0;
		
		if(diff >= 1 && val > 3){
			speed.setActualValue((60 * val) / diff);
			this.counter.set(0);
			lastTimestamp = timestamp; 
		} 
	}

	@Override
	public ControlValue getValue(String name) {
		//if(System.currentTimeMillis() - lastTimestamp > 1500){
		//	speed.setActualValue(0);
		//}else
		{
			//logger.info(this.name + ":=" + this.extReader.getValue(this.name));
			speed.setActualValue(extReader.getValue(this.name));
		}
		return speed;
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
