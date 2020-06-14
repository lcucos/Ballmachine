package com.webproject.pi;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.webproject.core.SystemState;
import com.webproject.pi.controllers.virtual.VirtualDeviceFactory;
import com.webproject.pi.ic2.I2CManager;

// java pi4j GPIO numbering scheme
// http://pi4j.com/pins/model-3b-rev1.html

public class PiSystem {
	final static Logger logger = LoggerFactory.getLogger(PiSystem.class);
	
	private HashMap<String, AbstractController> drivers = new HashMap<String, AbstractController>();
	private HashMap<String, AbstractSensorReader> sensors = new HashMap<String, AbstractSensorReader>();
	
	private GpioController gpio = null;
	private I2CManager i2cManager = new I2CManager();
	
	private String  configFolder;
	
	public PiSystem(){
	}
	
	public String getConfigFolder() {
		return configFolder;
	}
	public GpioController getGpioController() {
		return gpio;
	}
	public I2CManager getI2CManager(){
		return i2cManager;
	}
	public AbstractController getDriver(String name){
		return drivers.get(name);
	}
	public AbstractSensorReader getSensor(String name){
		return sensors.get(name);
	}

	public SystemState getSystemState() {
		SystemState state = new SystemState();
		for(String controlerID : drivers.keySet()){
			state.addControlValue(controlerID, drivers.get(controlerID).getValue(null));
		}
		return state;
	}

	public void init(String configFolder, boolean isVirtual) throws Exception{
		logger.info("Set configFolder::" + configFolder);
		this.configFolder = configFolder;
		
		IDeviceFactory factory = null;
		
		if(isVirtual == false){
			gpio = GpioFactory.getInstance();
			factory = new DeviceFactory();
		}else {
			factory = new VirtualDeviceFactory();
		}
		logger.info("Loading config");
		try {
			loadConfig(gpio, factory, configFolder);
		}catch(Exception ex) {
			logger.error("Exception loading configuration: " +  ex.getLocalizedMessage());
		}
	}
	
	
	private void loadConfig(GpioController gpio, IDeviceFactory factory, String configFolder) throws Exception {
		logger.info("Loading config : " + configFolder + "pi.json");
		byte[] jsonData = Files.readAllBytes(Paths.get(configFolder + "pi.json"));
		logger.info("Loaded jsonBytes : " +jsonData);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(jsonData);
		logger.info("Loaded json: " + rootNode);
		// load ic2 
		i2cManager.init(rootNode, gpio, factory);
		
		// load sensor readers
		ArrayNode arrSensors = (ArrayNode)rootNode.get("sensors");
		logger.info("Loading: " + arrSensors.size() + " sensor");
		for(int i=0;i<arrSensors.size();i++) {
			JsonNode node = arrSensors.get(i);

			String type = node.get("type").asText();
			AbstractSensorReader sensor = factory.createSensor(type);
			sensor.init(this, node);
									
			sensors.put(sensor.getName(), sensor);
			logger.info("Loaded: "+type + ":"+sensor.getName());
		}
		
		// load drivers
		ArrayNode arrDevices = (ArrayNode)rootNode.get("drivers");
		logger.info("Loading: " + drivers.size() + " drivers");
		for(int i=0;i<arrDevices.size();i++) {
			JsonNode node = arrDevices.get(i);

			String type = node.get("type").asText();
			AbstractController device = factory.createControler(type);
			device.init(this, node);
									
			drivers.put(device.getName(), device);
			logger.info("Loaded: "+type + ":"+device.getName());
		}
	}
	
	public static void main(String args[]) throws Exception {
		String freq = args[0];
		Integer valStop = Integer.parseInt(args[1]);
		
		PiSystem pi = new PiSystem();
		GpioController gpio = GpioFactory.getInstance();
	}

}
