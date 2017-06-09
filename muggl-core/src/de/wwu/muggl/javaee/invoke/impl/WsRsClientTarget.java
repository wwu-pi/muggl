package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.invoke.SpecialMethodUtil;
import de.wwu.muggl.javaee.ws.MugglWsRsClient;
import de.wwu.muggl.javaee.ws.MugglWsRsException;
import de.wwu.muggl.javaee.ws.MugglWsRsTarget;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

public class WsRsClientTarget implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		Object obj = frame.getOperandStack().pop();
		if(!(obj instanceof MugglWsRsClient)) {
			throw new SpecialMethodInvokeException("Expected the object reference to be of type MugglWsRsClient, but was: " + obj);
		}
		
		MugglWsRsClient mugglWsClient = (MugglWsRsClient)obj;
		
		Object requestUrlObject = parameters[1];
		if(!(requestUrlObject instanceof Objectref)) {
			throw new SpecialMethodInvokeException("Expected first parameter to be of type Objectref, but was: " + obj);
		}
		
		String requestUrl = SpecialMethodUtil.getInstance().getStringFromObjectref((Objectref)requestUrlObject);
		
		MugglWsRsTarget target = null;
		try {
			target = new MugglWsRsTarget((SymbolicVirtualMachine) frame.getVm(), mugglWsClient);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Error while generating MugglWsRsTarget", e);
		}
		
		target.setTargetUrl(requestUrl);
		
		frame.getOperandStack().push(target);
	}

}
