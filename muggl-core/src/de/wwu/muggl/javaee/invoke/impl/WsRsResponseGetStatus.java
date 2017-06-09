package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.ws.MugglWsRsResponse;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.vm.Frame;

public class WsRsResponseGetStatus implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		Object obj = frame.getOperandStack().pop();
		if(!(obj instanceof MugglWsRsResponse)) {
			throw new SpecialMethodInvokeException("Expected the object reference to be of type MugglWsRsResponse, but was: " + obj);
		}
		
		MugglWsRsResponse response = (MugglWsRsResponse)obj;
		
		NumericVariable status = response.getStatus();
		
		frame.getOperandStack().push(status);
	}

}
