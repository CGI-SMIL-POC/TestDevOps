package com.cgi.poc.dw.resources;

public class LoginRequest {

	/*
	 * LoginRequest
	 */
	private String username;
	private String password;

	/**
	 * constructor
	 */
	public LoginRequest() {

	}
	
	/**
	 * 
	 * @param username the user name
	 * @param password the password
	 */
	public LoginRequest(String username, String password) {

		this.username = username;
		this.password = password;
	}

	/**
	 * @return String the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
