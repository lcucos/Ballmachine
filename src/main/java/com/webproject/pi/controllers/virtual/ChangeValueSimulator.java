package com.webproject.pi.controllers.virtual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webproject.core.ControlValue;

public class ChangeValueSimulator {
	
	final static Logger logger = LoggerFactory.getLogger(ChangeValueSimulator.class);
	
	private double incVal = 0;
	private int incSteps = 5;

	public void changeValue(final ControlValue val, double absVal, int steps){
		
		double actual = val.getActualValue();
		incVal = (absVal - actual)/incSteps;
		logger.info("ChangeValue : " + val.getName() + " " + actual + " to: " + absVal);
		Thread th = new Thread (new Runnable(){
			@Override
			public void run() {
				for(int i = 0; i<incSteps;i++){
					val.setActualValue(val.getActualValue()+incVal);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		th.start();
	}
}
