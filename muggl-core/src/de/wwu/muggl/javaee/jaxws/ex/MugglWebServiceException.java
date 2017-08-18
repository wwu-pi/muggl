package de.wwu.muggl.javaee.jaxws.ex;

/**
 * @author Andreas Fuchs
 */
public class MugglWebServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MugglWebServiceException(String message, Throwable t) {
		super(message,t);
	}
}
