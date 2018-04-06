package de.wwu.muggl.javaee.logging;

import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;

public class SymoblicLogger extends ObjectrefVariable {

	public SymoblicLogger(String name, SymbolicVirtualMachine vm) {
		super(name, getRef(vm), vm);
	}

	private static InitializedClass getRef(SymbolicVirtualMachine vm) {
		ClassFile cf = null;
		try {
			cf = vm.getClassLoader().getClassAsClassFile("java.lang.Object");
		} catch(Exception e) {
			
		}
		InitializedClass ic = new InitializedClass(cf, vm);
		return ic;
	}

	
}
