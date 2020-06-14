package com.webproject.pi;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.webproject.core.ControlChange;
import com.webproject.core.ControlValue;

public abstract class AbstractController {
	final static Logger logger = LoggerFactory.getLogger(AbstractController.class);

	public static final int REQUEST_NONE  = 0;
	public static final int REQUEST_ABORT = 1;
	
	protected int currentState;
	protected int reguestedState = REQUEST_NONE;
	protected String name ;
	
	protected double tolerancePercent = 0.1;
	protected ValueControlMap valueControlMap = null;
	
	private ConcurrentLinkedQueue<ControlChange> commandQueue = new ConcurrentLinkedQueue<ControlChange>();

	
	public AbstractController(){
	}

	public void init(PiSystem piSystem, JsonNode node) throws Exception {
		name = node.get("name").asText();
		
		String mapFile=node.get("controlMapFile")!=null?node.get("controlMapFile").asText():null;
		if(mapFile!=null && mapFile.length()>0) {
			loadMapFile(piSystem.getConfigFolder()+mapFile);
		}

		JsonNode startup = node.get("startup");

		allocatePins(piSystem, node.get("pins"));

		// start thread
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				workerThreadMain();
			}
		});
		th.start();
		
		// set startup value
		if(startup!=null) {
			Float value = startup.get("value").floatValue();
			boolean isRelative = startup.get("relative").asBoolean();
			setValues(new ControlChange(name, isRelative, value, null));
			logger.info("Set startup default: "+name + ": value="+ value + ", isRelative="+isRelative);
		}

	}
	
	protected void workerThreadMain() {
		while(true) {
			try {
				// check command queue
				while(commandQueue.isEmpty()==false) {
					setValuesInternal(commandQueue.remove());
				}
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}

	public boolean isRequestAbort(){
		if(reguestedState==REQUEST_ABORT){
			return true;
		}
		return false; 
	}
	
	public void clearAbort(){
		reguestedState = REQUEST_NONE;
	}
	
	public void abort(){
		reguestedState = REQUEST_ABORT;
	}

	protected abstract void setValuesInternal(ControlChange obj) throws InterruptedException ;

	public void setValues(ControlChange obj) {
		commandQueue.add(obj);
	}


	public abstract ControlValue getValue(String name);
	
	public void loadMapFile(String fileName) throws Exception {
		ValueControlMap mapControl = new ValueControlMap();
		mapControl.loadFile(fileName);
		setValueController(mapControl);
	}
	
	public void setName(String string) {
		name = string;
	}
	
	public String getName(){
		return name;
	}
	
	public void setValueController(ValueControlMap map) {
		valueControlMap = map;
	}

	public void allocatePins(PiSystem pi, JsonNode pins) throws Exception{
	}

	public int calibrate() throws Exception {
		return 0;
	}
}
