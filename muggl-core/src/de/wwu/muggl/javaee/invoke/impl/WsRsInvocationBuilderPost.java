package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.invoke.SpecialMethodUtil;
import de.wwu.muggl.javaee.ws.MugglWsRsException;
import de.wwu.muggl.javaee.ws.MugglWsRsResponse;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

public class WsRsInvocationBuilderPost implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		
		// this must be a class object reference
		// that gives the return type
		Object responseClassObj = parameters[2]; 
		
		if(!(responseClassObj instanceof Objectref)) {
			throw new SpecialMethodInvokeException("Expected second argument to be of type Objectref, but was: " + responseClassObj);
		}
		
		Objectref responseClassObjRef = (Objectref)responseClassObj;
		
		String responseClassName = SpecialMethodUtil.getInstance().getClassNameFromObjectRef(responseClassObjRef);
		
		if(responseClassName.equals("javax.ws.rs.core.Response")) {
			MugglWsRsResponse mugglResponse = null;
			try {
				mugglResponse = new MugglWsRsResponse(vm);
			} catch (MugglWsRsException e) {
				throw new SpecialMethodInvokeException("Error while generating MugglWsRsResponse", e);
			}
			frame.getOperandStack().push(mugglResponse);
			return;
		}
		
		ClassFile responseClassFile = null;
		try {
			responseClassFile = vm.getClassLoader().getClassAsClassFile(responseClassName);
		} catch (ClassFileException e) {
			throw new SpecialMethodInvokeException("Could not find class file: " + responseClassName, e);
		}
		Objectref responseObjRef = vm.getAnObjectref(responseClassFile);
		
		// TODO: hier ein wrapper für den response object ref machen!
		
		ObjectrefVariable respVar = new ObjectrefVariable("response", responseObjRef.getInitializedClass(), vm);
		
		frame.getOperandStack().push(respVar);
	}

	
	
}
