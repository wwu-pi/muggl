package de.wwu.muggl.javaee.ws;

import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.ReferenceValue;

public abstract class MugglWsRs implements ReferenceValue {

	private static int globalInitNumber = 0;
	
	protected int initNumber;
	
	protected InitializedClass initializedClass;
	
	protected String jaxRsClassName;
	
	protected SymbolicVirtualMachine vm;
	
	MugglWsRs(String jaxRsClassName, SymbolicVirtualMachine vm) throws MugglWsRsException {
		this.initNumber = globalInitNumber++;
		this.vm = vm;
		this.jaxRsClassName = jaxRsClassName;
		try {
			ClassFile cf = vm.getClassLoader().getClassAsClassFile(jaxRsClassName);
			this.initializedClass = new InitializedClass(cf, vm);
		} catch (ClassFileException e) {
			throw new MugglWsRsException("Cannot initialize Muggl JAX-RS Client, since class "+jaxRsClassName+" cannot be found. Please check class path.", e);
		}
	}
	
	@Override
	public String toString() {
		return "Muggl-Wrapper for: " + this.jaxRsClassName + "(initNumber="+this.initNumber+")";
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public String getName() {
		return "Muggl-Wrapper for: " + this.jaxRsClassName;
	}

	@Override
	public String getSignature() {
		return this.getClass().getName();
	}

	@Override
	public InitializedClass getInitializedClass() {
		return this.initializedClass;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public long getInstantiationNumber() {
		return initNumber;
	}

	@Override
	public ReferenceValue clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
