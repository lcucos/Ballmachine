package com.webproject.pi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueControlMap {
	final static Logger logger = LoggerFactory.getLogger(ValueControlMap.class);
	private File file = null;
	
	// sorted array by value. This works well if the data is fairly equally distributed around integer values
	private ArrayList<SimpleControlValue> map = new ArrayList<SimpleControlValue>();
	
	public void loadFile(String fileName) throws Exception {
		file = new File(fileName);
		if(file.exists()==false) {
			logger.info("File "+ fileName+" doesn't exist!");
			return;
		}
		
		// read from file
        BufferedReader br = null;
        String strLine = "";
        br = new BufferedReader( new FileReader(fileName));
        while( (strLine = br.readLine()) != null){
        	strLine = strLine.trim();
        	if(strLine.startsWith("#")){
        		continue;
        	}
        	String vals[] = strLine.split(",");
        	try {
        		SimpleControlValue cv = new SimpleControlValue(vals);
        		map.add(cv);
        	}catch(Exception ex) {
        		logger.error(ex.getLocalizedMessage());
        	}
        }
        br.close();
        logger.info("Read: " + map.size() + " control values pairs");
	}
	
	// binary search: get value for control number	
	public SimpleControlValue getValue(int controlNumber) {
		if(map.isEmpty()) {
			return null;
		}
		if(controlNumber<= map.get(0).control) {
			return map.get(0);
		}
		if(controlNumber >= map.get(map.size()-1).control) {
			return map.get(map.size()-1);
		}
		int lowIndex=0;
		int highIndex=map.size()-1;
		
		// binary search
		while(true) {
			if(highIndex - lowIndex < 2 ) {
				break;
			}
			int mid = lowIndex+ (highIndex - lowIndex)/2;
			int crtVal =map.get(mid).control;
			if(crtVal > controlNumber) {
				highIndex = mid;
			}else if(crtVal < controlNumber){
				lowIndex = mid;
			}else {
				return map.get(mid);
			}
		}
		SimpleControlValue cvalA = map.get(lowIndex);
		SimpleControlValue cvalB = map.get(highIndex);
		
		double val = (cvalA.value + (cvalB.value - cvalA.value) * (controlNumber - cvalA.control)/(cvalB.control-cvalA.control));
		return new SimpleControlValue(controlNumber, val);		
	}
	
	// binary search: get control number for the target value
	public SimpleControlValue getControl(double val) {
		if(map.isEmpty()) {
			return null;
		}

		if(val<= map.get(0).value) {
			return map.get(0);
		}
		if(val >= map.get(map.size()-1).value) {
			return map.get(map.size()-1);
		}
		int lowIndex=0;
		int highIndex=map.size()-1;
		
		// binary search
		while(true) {
			if(highIndex - lowIndex < 2 ) {
				break;
			}
			int mid = lowIndex+ (highIndex - lowIndex)/2;
			double crtVal =map.get(mid).value;
			if(crtVal > val) {
				highIndex = mid;
			}else if(crtVal < val){
				lowIndex = mid;
			}else {
				return map.get(mid);
			}
		}
		SimpleControlValue cvalA = map.get(lowIndex);
		SimpleControlValue cvalB = map.get(highIndex);
		
		int control = (int)( cvalA.control + (cvalB.control - cvalA.control) * (val - cvalA.value)/(cvalB.value-cvalA.value));
		return new SimpleControlValue(control, val);
	}
	
	public void update(ArrayList<SimpleControlValue> map) throws Exception{
		// save file
        try {
            FileWriter fw = new FileWriter(file);
            for(SimpleControlValue cv:  map) {
            	fw.write(""+cv.control + ","+cv.value+"\n");
            }
            fw.flush();
            fw.close();
            this.map = map;
        } catch (IOException iox) {
            iox.printStackTrace();
        }
	}
	
	public static void main(String arg[]) throws Exception {
		ValueControlMap app = new ValueControlMap();
		app.loadFile("./verticalStepperMap.csv");
		try {
			double val = 18.8;
			for(int i= 0;i<5;i++) {
				int contr = app.getControl(val).control;
				System.out.println("control("+val+")=" + contr);
				val=app.getValue(contr).value;
			}
			
			System.out.println("control(28.2)=" + app.getControl(28.2).control);
			System.out.println("control(7.3)" + app.getControl(7.3).control);
			
			System.out.println("control(18.8)=" + app.getControl(18.8).control);
			
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
}
