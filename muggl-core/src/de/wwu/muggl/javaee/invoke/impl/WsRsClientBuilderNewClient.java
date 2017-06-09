package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.ws.MugglWsRsClient;
import de.wwu.muggl.javaee.ws.MugglWsRsException;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public class WsRsClientBuilderNewClient implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsClient mugglRSClient = null;
		try {
			mugglRSClient = new MugglWsRsClient((SymbolicVirtualMachine) frame.getVm());
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Cannot generate a Muggl JAX-RS Client.", e);
		}
		
		frame.getOperandStack().push(mugglRSClient);
	}

}
