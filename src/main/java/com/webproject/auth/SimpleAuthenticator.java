package com.webproject.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;


//import com.google.common.base.Optional;
import com.webproject.core.User;
import com.webproject.db.UserDAO;
import com.webproject.utils.Utils;

public class SimpleAuthenticator implements Authenticator<BasicCredentials, User> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleAuthenticator.class);
    private User uberUser;
    private UserDAO usersDao;
    
    public SimpleAuthenticator(User uber, UserDAO userDao){
    	uberUser = uber;
    	usersDao = userDao;
    }
    
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
    	String username = credentials.getUsername();
    	// pass should come as md5 .... eventually
    	String passwordMD5 = Utils.getMD5(credentials.getPassword());
    	
    	logger.info("username = " +username);
    	// find the user in the local db
    	com.google.common.base.Optional<User> user = null;
    	if(usersDao!=null){
    		user = usersDao.findByUsernameManaged(username);
    	}
    	
    	if(user==null || !user.isPresent()){
	    	// check uberuser
	    	if(uberUser!=null && uberUser.getName().equals(username) && uberUser.getMD5Password().equals(passwordMD5)){
	    		return Optional.of(uberUser);
	    	}
    	}else{
    		User crtUser = user.get();
    		logger.info("username= " +username + ", role="+user.get().getName());
	    	if(crtUser.getMD5Password().equals(passwordMD5)){
	    		return Optional.of(crtUser);
	    	}
    	}
        return Optional.empty();
    }
}
