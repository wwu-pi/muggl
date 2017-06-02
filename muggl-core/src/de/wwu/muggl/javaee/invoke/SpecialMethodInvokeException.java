package de.wwu.muggl.javaee.invoke;

import de.wwu.muggl.configuration.MugglException;

/**
 * An exception class for the special method invocations.
 * 
 * @author Andreas Fuchs
 */
public class SpecialMethodInvokeException extends MugglException {

	private static final long serialVersionUID = 1L;

	public SpecialMethodInvokeException(String message) {
		super(message);
	}
	
	public SpecialMethodInvokeException(String message, Throwable t) {
		super(message, t);
	}
}
