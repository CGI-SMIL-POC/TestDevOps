package com.cgi.poc.dw.resources;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgi.poc.dw.auth.DBAuthenticator;
import com.cgi.poc.dw.core.User;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
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
    private final DBAuthenticator dbAuthenticator;

    /**
     * Constructor to initialize DAO.
     *
     * @param dbAuthenticator the authenticator.
     */
    public Authentication(final DBAuthenticator dbAuthenticator) {
        this.dbAuthenticator = dbAuthenticator;
    }

    /**
     * Method returns all assets stored by a particular user.
     *
     * @param loginRequest LoginRequest the login request
     * @return LoginResponse the login response.
     */
    @POST
    @UnitOfWork
    public LoginResponse login(LoginRequest loginRequest) {
    	
    	LoginResponse loginResponse = new LoginResponse(); 
    	loginResponse.setStatus(AUTH_FAILED);
    	try{
    		
    		BasicCredentials credentials = new BasicCredentials(loginRequest.getUsername(), loginRequest.getPassword());
    		
    		final Optional<User> principal = dbAuthenticator.authenticate(credentials);
            if (principal.isPresent()) {
            	loginResponse.setStatus(AUTH_SUCCESS);
            }
    	} catch (AuthenticationException e) {
    		LOGGER.warn("Error authenticating credentials", e);
            throw new InternalServerErrorException();
        }
        
        return loginResponse;
        
    }

}
