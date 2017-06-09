package de.wwu.muggl.javaee.ws;

import de.wwu.muggl.configuration.MugglException;

public class MugglWsRsException extends MugglException {

	private static final long serialVersionUID = 1L;

	public MugglWsRsException(String s) {
		super(s);
	}
	
	public MugglWsRsException(String s, Throwable t) {
		super(s, t);
	}
		
}
