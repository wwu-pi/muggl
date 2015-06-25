package de.wwu.testtool.expressions;

import de.wwu.testtool.exceptions.TesttoolException;

/**
 * @author Christoph Lembeck
 */
@SuppressWarnings("serial")
public class TypeCheckException extends TesttoolException {

    /**
     * Creates a new instance of TypeCheckException.
     * @param msg the message that should be stored inside the exception.
     */
    public TypeCheckException(String msg) {
	super(msg);
    }

}
