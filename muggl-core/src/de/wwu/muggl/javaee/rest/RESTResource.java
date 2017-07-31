package de.wwu.muggl.javaee.rest;

import de.wwu.muggl.javaee.ws.MugglRESTResponse;
import de.wwu.muggl.javaee.ws.MugglWsRsTarget;

/**
 * A REST resource wrapper type.
 * 
 * @author Andreas Fuchs
 */
public class RESTResource {

	protected MugglWsRsTarget target;
	protected MugglRESTResponse response;
	protected RequestType requestType;
	
	public RESTResource(MugglWsRsTarget target) {
		this.target = target;
	}

	public void setResponse(MugglRESTResponse response) {
		this.response = response;
	}
		
	public MugglRESTResponse getResponse() {
		return this.response;
	}
	
	public MugglWsRsTarget getTarget() {
		return this.target;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	
	public RequestType getRequestType() {
		return this.requestType;
	}
}
