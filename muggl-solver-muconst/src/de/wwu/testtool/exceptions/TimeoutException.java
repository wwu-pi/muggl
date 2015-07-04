package de.wwu.testtool.exceptions;

import de.wwu.testtool.conf.TesttoolConfig;

/**
 * @author Christoph Lembeck
 */
@SuppressWarnings("serial")
public class TimeoutException extends TesttoolException {

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message.
     */
    public TimeoutException(String message){
	super(message);
	if (TesttoolConfig.getLogger().isDebugEnabled())
	    TesttoolConfig.getLogger().debug("TimeoutException thrown: " + message);
    }
}