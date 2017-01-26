package com.cgi.poc.dw.resources;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgi.poc.dw.core.User;
import com.cgi.poc.dw.db.UserDAO;

import io.dropwizard.hibernate.UnitOfWork;

/**
 * A class to serve assets data to users.
 *
 */
@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Authentication {

	/**
	 * authentication failed constant
	 */
	private static final String AUTH_FAILED = "FAILED";
    
	/**
	 * authentication success constant
	 */
    private static final String AUTH_SUCCESS = "SUCCESS";
    
	/**
     * Logger.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(Authentication.class);
    /**
     * DAO to manipulate assets.
     */
    private final UserDAO userDAO;

    /**
     * Constructor to initialize DAO.
     *
     * @param userDAO DAO to manipulate user.
     */
    public Authentication(final UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Method returns all assets stored by a particular user.
     *
     * @param username Authenticated user with whose assets we work.
     * @param password
     * @return list of assets stored by a particular user.
     */
    @POST
    @UnitOfWork
    public String login(@PathParam("username") String username, @PathParam("password") String password) {
    	
    	String authentificationStatus = AUTH_FAILED;
    	
    	LOGGER.debug("log user : " + username);
        Optional<User> userRetrieved = userDAO.findByUsernameAndPassword(username, password);
        
        if(userRetrieved.isPresent()){
        	authentificationStatus = AUTH_SUCCESS;
        }
        
        return authentificationStatus;
        
    }

}
