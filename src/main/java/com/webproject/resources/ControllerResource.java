package com.webproject.resources;

import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webproject.core.ControlChange;
import com.webproject.core.ControlValue;
import com.webproject.core.SystemState;
import com.webproject.pi.AbstractController;
import com.webproject.pi.PiSystem;

@Path("/controller")
@Produces(MediaType.APPLICATION_JSON)
public class ControllerResource {
	final static Logger logger = LoggerFactory.getLogger(ControllerResource.class);
	private PiSystem piSystem;
	
    public ControllerResource(PiSystem piSystem) {
		this.piSystem=piSystem;
	}

	@POST
    @UnitOfWork
    public SystemState setValues(ControlChange obj) {
    	//logger.info(obj.toString());
        AbstractController controller = piSystem.getDriver(obj.getDriverID());
        if(controller!=null){
        	controller.setValues(obj);
        }
        return piSystem.getSystemState();
    }

    @GET
    @Path("/{cid}")
    public ControlValue getValue(@PathParam("cid") String objId) {
    	AbstractController controller = piSystem.getDriver(objId);
    	ControlValue val = controller.getValue(null);
    	return val;
    }    


    @GET
    @Path("/calibrate/{cid}")
    public int calibrate(@PathParam("cid") String objId) {
    	AbstractController controller = piSystem.getDriver(objId);
    	int val = -2;
		try {
			val = controller.calibrate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return val;
    }    

    @GET
    @Path("/state")
    public SystemState getState() {
    	SystemState state = piSystem.getSystemState();
    	//logger.info(state.toString());
    	return state;
    }    


    @GET
    @UnitOfWork
    public ControlChange listUsers() {
    	logger.info("list control values");
        return null;
    }
}
