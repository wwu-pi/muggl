package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.ws.MugglWsRsEntity;
import de.wwu.muggl.javaee.ws.MugglWsRsException;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public class WsRsClientEntity implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		
		MugglWsRsEntity wsEntity = null;
		try {
			wsEntity = new MugglWsRsEntity((SymbolicVirtualMachine) frame.getVm());
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Error while generating MugglWsRsEntity", e);
		}
		
		frame.getOperandStack().push(wsEntity);
	}

}
