package com.webproject.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;

import com.google.common.base.Optional;
import com.webproject.core.User;

public class UserDAO extends AbstractDAO<User> {
	SessionFactory factory ;
	public UserDAO(SessionFactory factory) {
        super(factory);
        this.factory = factory;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.fromNullable(get(username));
    }

    public Optional<User> findByUsernameManaged(String username) {
    	Optional<User> retVal = Optional.fromNullable(null);
		final Session ses = factory.openSession();
		try{
			ManagedSessionContext.bind(ses);
			retVal = Optional.fromNullable(get(username));
		}finally{
			ses.close();
		}
        return retVal;
    }

    public User create(User user) {
        return persist(user);
    }

    public List<User> findAll() {
        return list(namedQuery("com.webproject.core.User.findAll"));
    }

	public void deleteByUsername(User usr) {
		currentSession().delete(usr);
	}

	public void update(User prevuserInfo, User updates) {
		prevuserInfo.update(updates);
		currentSession().update(prevuserInfo);
	}
}
