package com.webproject.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webproject.core.User;

import io.dropwizard.auth.Authorizer;

public class SimpleAuthorizer implements Authorizer<User> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleAuthorizer.class);


    @Override
    public boolean authorize(User user, String role) {
    	//logger.info("user="+user.getName() + " role="+user.getRole() + ", alowed role="+role);

    	// TODO: link roles and permissions (assume non-overlapping roles)
    	if(role.contains(user.getRole())){
    		return true;
    	}
    	return false;
    }
}
