package de.wwu.muggl.javaee.jpa.cstr;

import de.wwu.muggl.configuration.MugglException;

public class EntityConstraintException extends MugglException {

	private static final long serialVersionUID = 1L;

	public EntityConstraintException(String m) {
		super(m);
	}
	
	public EntityConstraintException(Throwable t, String m) {
		super(m,t);
	}
	
}
