package de.wwu.muggl.symbolic.var.meta;

/**
 * Exception for reference variables.
 */
public class ReferenceVariableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReferenceVariableException(Throwable e) {
		super(e);
	}

	public ReferenceVariableException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public ReferenceVariableException(String msg) {
		super(msg);
	}

}
