package de.wwu.muggl.javaee.rest;

import de.wwu.muggl.javaee.ws.MugglRESTResponse;

public class RESTResource {

	protected String endpoint;
	protected MugglRESTResponse response;
	
	public RESTResource(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setResponse(MugglRESTResponse response) {
		this.response = response;
	}
	
}
