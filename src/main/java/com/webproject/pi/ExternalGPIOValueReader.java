package com.webproject.pi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.webproject.core.ControlChange;

public class ExternalGPIOValueReader extends AbstractSensorReader{
	final static Logger logger = LoggerFactory.getLogger(ExternalGPIOValueReader.class);

	private HashMap<String, Double> values = new HashMap<String, Double>();
	
	public void init(PiSystem piSystem, JsonNode node) {
		super.init(piSystem, node);
		start();
	}

	public void start(){
        Thread th = new Thread(new Runnable(){
			public void run() {
				try {
					while(true){
						Thread.sleep(100);
						updateSpeed();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        });
        th.start();
	}

	protected void updateSpeed()  {
        URL url;
		try {
			url = new URL("http://127.0.0.1:5000/");

	        //make connection
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
	        //use post mode
	
	        String val = "";
	        //get result
	        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String l = null;
	        while ((l=br.readLine())!=null) {
	            val+=l;
	        }
	        br.close();
	        // only two speeds
	        
	        int pos = val.indexOf(',');
	        Double top = Double.parseDouble(val.substring(0, pos));
	        Double bot = Double.parseDouble(val.substring(pos+1));
	        //logger.info("LC:"+val);
	        conn.disconnect();
	        values.put("topSpeed", top);
	        values.put("botSpeed", bot);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	public double getValue(String name){
		Double ret =  values.get(name);
		if(ret==null){
			return 0;
		}
		return ret;
	}
		
	public static void main(String str[]) throws Exception{
		ExternalGPIOValueReader app = new ExternalGPIOValueReader();
		app.updateSpeed();
	}

}
