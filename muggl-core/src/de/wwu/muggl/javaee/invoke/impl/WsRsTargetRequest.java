package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.invoke.SpecialMethodUtil;
import de.wwu.muggl.javaee.ws.MugglWsRsException;
import de.wwu.muggl.javaee.ws.MugglWsRsInvocationBuilder;
import de.wwu.muggl.javaee.ws.MugglWsRsTarget;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

public class WsRsTargetRequest implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		Object obj = frame.getOperandStack().pop();
		if(!(obj instanceof MugglWsRsTarget)) {
			throw new SpecialMethodInvokeException("Expected the object reference to be of type MugglWsRsTarget, but was: " + obj);
		}
		
		MugglWsRsTarget target = (MugglWsRsTarget)obj;
		

		
		MugglWsRsInvocationBuilder invocationBuilder;
		try {
			invocationBuilder = new MugglWsRsInvocationBuilder((SymbolicVirtualMachine) frame.getVm(), target);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Error while generating MugglWsRsInvocationBuilder", e);
		}
		
		
		
		Object acceptedResponseTypesObj = parameters[1]; // must be an array reference
		
		if(!(acceptedResponseTypesObj instanceof Arrayref)) {
			throw new SpecialMethodInvokeException("Expected first argument to be of type Arrayref, but was: " + acceptedResponseTypesObj);
		}
		
		// get the accepted response types from the array
		Arrayref acceptedResponseTypesArray = (Arrayref)acceptedResponseTypesObj;
		for(int i=0; i<acceptedResponseTypesArray.length; i++) {
			Object o = acceptedResponseTypesArray.getElement(i);
			if(!(o instanceof Objectref)) {
				throw new SpecialMethodInvokeException("Expecte elements in array of first parameter to be object reference (of type java.lang.String), but was: " + o);
			}
			Objectref objRef = (Objectref)o;
			String s = SpecialMethodUtil.getInstance().getStringFromObjectref(objRef);
			invocationBuilder.addAcceptedResponseType(s);
		}
		
		
		frame.getOperandStack().push(invocationBuilder);
	}
	
}
