package com.webproject.pi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.webproject.core.ControlChange;
import com.webproject.core.ControlValue;


public class StepperController extends AbstractController implements GpioPinListenerDigital {
	protected GpioPinDigitalOutput coil_A_1_pin;
	protected GpioPinDigitalOutput coil_A_2_pin;
	protected GpioPinDigitalOutput coil_B_1_pin;
	protected GpioPinDigitalOutput coil_B_2_pin;
	private   GpioPinDigitalInput  limitSensor_pin;
	
	protected static final int DELAY_MS = 2; 
	
	protected static final int MOVE_NONE =  0;
	protected static final int MOVE_UP   =  1;
	protected static final int MOVE_DOWN = -1;
	
	// state info
	protected int stepsFromZeroPos = -1; // steps from zero, -1 means is not calibrated 
	protected ControlValue angle = new ControlValue("angle",0.1);

	final static Logger logger = LoggerFactory.getLogger(StepperController.class);
	
	// steps per angle = 200*50/360 = 27.777777778
	public static final float STEPS_ANGLE = 0.035f;//27.777777778f;
	private static final int RETREET_STEPS = 40;
		
	// last request
	protected int direction = MOVE_NONE; // -1 is moving Down, +1 is moving Up, 0 is stationary
	protected int MAX_STEPS_FROM_ZERO = 0;
	
	
	public StepperController() {
	}

	private void setStep(PinState a1, PinState a2, PinState b1, PinState b2){
		coil_A_1_pin.setState(a1);
		coil_A_2_pin.setState(a2);
		coil_B_1_pin.setState(b1);
		coil_B_2_pin.setState(b2);
	}
	
	public void allocatePins(PiSystem pi, JsonNode pins) throws Exception {
		coil_A_1_pin = pi.getGpioController().provisionDigitalOutputPin(PinMapper.getInstance().getBCommPinFromGPIO(pins.get("A+").asText()), "A+");
		coil_A_2_pin = pi.getGpioController().provisionDigitalOutputPin(PinMapper.getInstance().getBCommPinFromGPIO(pins.get("A-").asText()), "A-");
		coil_B_1_pin = pi.getGpioController().provisionDigitalOutputPin(PinMapper.getInstance().getBCommPinFromGPIO(pins.get("B+").asText()), "B+");
		coil_B_2_pin = pi.getGpioController().provisionDigitalOutputPin(PinMapper.getInstance().getBCommPinFromGPIO(pins.get("B-").asText()), "B-");
		String pinLimit = pins.get("limit")!=null?pins.get("limit").asText():null;
		logger.info(getName() + ": A+ pin= " + PinMapper.getInstance().getBCommPinFromGPIO(pins.get("A+").asText()) );
		logger.info(getName() + ": A- pin= " + PinMapper.getInstance().getBCommPinFromGPIO(pins.get("A-").asText()) );
		logger.info(getName() + ": B+ pin= " + PinMapper.getInstance().getBCommPinFromGPIO(pins.get("B+").asText()) );
		logger.info(getName() + ": B- pin= " + PinMapper.getInstance().getBCommPinFromGPIO(pins.get("B-").asText()) );
		
		if(pinLimit!=null) {
			Pin limit = PinMapper.getInstance().getBCommPinFromGPIO(pinLimit);
        	limitSensor_pin = pi.getGpioController().provisionDigitalInputPin(limit,        // PIN NUMBER
        			PinPullResistance.PULL_UP ); 
        	limitSensor_pin.setShutdownOptions(true);
        	limitSensor_pin.addListener(this);	
        	logger.info(getName() + ": set limit pin " + limitSensor_pin.getName());
		}
	}

	public int spinConterClockWise(int steps, int delay) throws InterruptedException{
		if(isRequestAbort()){
			// nothing happens until the request abort was cleared
			return 0 ;
		}
		return spinConterClockWiseInternal(steps, delay, false);
	}
	
	public int calibrate() throws InterruptedException {
		if(valueControlMap==null) {
			return -1;
		}
		logger.info(getName() + " start calibrating ");
		spinClockWise(Integer.MAX_VALUE, DELAY_MS);
		logger.info(getName() + " end calibrating ");
		return 1;
	}
	
	
	private int spinConterClockWiseInternal(int steps, int delay, boolean bForce) throws InterruptedException{
		int actualSteps = 0;
		direction = MOVE_DOWN;
		logger.info(getName() + ": request move "+direction+" " + steps + " (abs pos = " + stepsFromZeroPos + ")");
        for(int i = 0; i<steps;i++){
        	setStep(PinState.HIGH, PinState.LOW, PinState.LOW, PinState.HIGH);
        	Thread.sleep(delay);
        	setStep(PinState.LOW, PinState.HIGH, PinState.LOW, PinState.HIGH);
        	Thread.sleep(delay);
        	setStep(PinState.LOW, PinState.HIGH, PinState.HIGH, PinState.LOW);
        	Thread.sleep(delay);
        	setStep(PinState.HIGH, PinState.LOW, PinState.HIGH, PinState.LOW);
        	Thread.sleep(delay);        	
        	actualSteps++;
        	stepsFromZeroPos++;
        	if(isRequestAbort() && bForce==false){
        		spinClockWiseInternal(RETREET_STEPS, DELAY_MS, true);
        		clearAbort();
        		break;
        	}
        }
        logger.info(getName() + ": successful moved "+direction + " " + steps + " (abs pos = " + stepsFromZeroPos + ")");
        return actualSteps;		
	}	
	
	public int spinClockWise(int steps, int delay) throws InterruptedException{
		if(isRequestAbort()){
			// nothing happens until the request abort was cleared
			return 0 ;
		}
		return spinClockWiseInternal(steps, delay, false);
	}
	
	public int spinClockWiseInternal(int steps, int delay, boolean bForce) throws InterruptedException{
		int actualSteps = 0;
		direction = MOVE_UP;
		logger.info(getName() + ": request move "+direction+" " + steps + " (abs pos = " + stepsFromZeroPos + ")");
        for(int i = 0; i<steps;i++){ 
        	setStep(PinState.HIGH, PinState.LOW, PinState.HIGH, PinState.LOW);
        	Thread.sleep(delay);
        	setStep(PinState.LOW, PinState.HIGH, PinState.HIGH, PinState.LOW);
        	Thread.sleep(delay);
        	setStep(PinState.LOW, PinState.HIGH, PinState.LOW, PinState.HIGH);
        	Thread.sleep(delay);
        	setStep(PinState.HIGH, PinState.LOW, PinState.LOW, PinState.HIGH);
        	Thread.sleep(delay);        	
        	actualSteps++;
        	stepsFromZeroPos--;
        	if(isRequestAbort() && bForce==false){
        		spinConterClockWiseInternal(RETREET_STEPS, DELAY_MS, true);
        		clearAbort();
        		break;
        	}
        }
        logger.info(getName() + ": successful moved "+direction + " " + steps + " (abs pos = " + stepsFromZeroPos + ")");
        return actualSteps;
	}
		
	protected void setValuesInternal(ControlChange obj) throws InterruptedException {
		if(stepsFromZeroPos<0) {
			calibrate();
		}
		//angle.setRequestValue(obj.getValue());
		//angle.setStatus(obj.getStatus());

		//double angleVal = obj.getValue();
		if(valueControlMap==null) {
			logger.error(getName()+" :  valueControlMap missing");
			return;
		}
		double actualAngleValue = valueControlMap.getValue(stepsFromZeroPos).value; 
		double angleVal = obj.getRequestedAbsoluteValue(actualAngleValue);
		angle.setRequestValue(angleVal);
		
		int newPosition = valueControlMap.getControl(angleVal).control;
		int steps = stepsFromZeroPos - newPosition;
		
		logger.info(getName()+": request set absolute angle : " + angleVal + " move from: " + stepsFromZeroPos + " to: " + newPosition + ": steps="+steps);
		//double steps = angleVal / STEPS_ANGLE;
		
		if(steps>0){
			spinClockWise((int)steps, DELAY_MS);
		}else{
			spinConterClockWise((-1)*(int)steps, DELAY_MS);
		}
	}

	@Override
	public ControlValue getValue(String name) {
		if(valueControlMap!=null) {
			double actualAngleValue = valueControlMap.getValue(stepsFromZeroPos).value; 
			angle.setActualValue(actualAngleValue);
		}
		return angle;
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		if(event.getState()==PinState.LOW){
			logger.warn(getName() + ": reached " + (direction==MOVE_UP ? "UP limit" : ((direction==MOVE_DOWN)? "DOWN limit": "Unknown limit")) + " (abs pos = " + stepsFromZeroPos +") " + event.getState());
			if(direction==MOVE_UP){
				// we reached zero steps
				int prevSteps = stepsFromZeroPos;
				logger.info(getName() + "Reached zero: prevSteps=" + prevSteps);
				stepsFromZeroPos = 0;
			}
			abort();
		}
	}
	
	
}
