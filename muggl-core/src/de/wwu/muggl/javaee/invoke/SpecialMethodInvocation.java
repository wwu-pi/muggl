package de.wwu.muggl.javaee.invoke;

import de.wwu.muggl.vm.Frame;

/**
 * Interface for a special method invocation execution.
 * @author Andreas Fuchs
 */
public interface SpecialMethodInvocation {

	/**
	 * Execute special.
	 * @param frame the current frame on which the method should be executed special.
	 * @param parameters the parameters of the method to invoke
	 */
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException;
}
