package de.wwu.muggl.javaee.jpa;

import de.wwu.muggl.configuration.MugglException;

/**
 * Exception class for the symbolic database.
 * @author Andreas Fuchs
 */
public class SymbolicDatabaseException extends MugglException {

	private static final long serialVersionUID = 1L;

	public SymbolicDatabaseException(String s) {
		super(s);
	}
	
	public SymbolicDatabaseException(String s, Throwable t) {
		super(s,t);
	}
}
