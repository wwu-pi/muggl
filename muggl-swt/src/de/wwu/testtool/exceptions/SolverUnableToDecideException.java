package de.wwu.testtool.exceptions;

import de.wwu.testtool.conf.TesttoolConfig;

/**
 * @author Christoph Lembeck
 */
@SuppressWarnings("serial")
public class SolverUnableToDecideException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message.
     */
    public SolverUnableToDecideException(String message){
	super(message);
	if (TesttoolConfig.getLogger().isDebugEnabled())
	    TesttoolConfig.getLogger().debug("SolverUnableToDecideException thrown: " + message);
    }

}
