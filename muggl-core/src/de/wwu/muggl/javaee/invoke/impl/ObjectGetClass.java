package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.invoke.SpecialMethodUtil;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

public class ObjectGetClass implements SpecialMethodInvocation {
	
	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		
		Object obj = frame.getOperandStack().pop();
		if(obj instanceof Objectref) {
			Objectref objRef = (Objectref)obj;
			String className = objRef.getInitializedClass().getClassFile().getName();
			
			Objectref classRef = SpecialMethodUtil.getInstance().getClassObjectRef(vm, className);
			
			frame.getOperandStack().push(classRef);
		}
		
		else if(obj instanceof Arrayref) {
			Arrayref arrRef = (Arrayref)obj;
			String className = "[L"+arrRef.getInitializedClass().getClassFile().getName()+";";
			
			Objectref classRef = SpecialMethodUtil.getInstance().getClassObjectRef(vm, className);
			
			frame.getOperandStack().push(classRef);
		}
		
		else {
			throw new SpecialMethodInvokeException("Expected Objectref or Arrayref, but was:" + obj);
		}
	}
}
