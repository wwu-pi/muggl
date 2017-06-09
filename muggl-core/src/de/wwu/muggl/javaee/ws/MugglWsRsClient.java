package de.wwu.muggl.javaee.ws;

import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;


public class MugglWsRsClient extends MugglWsRs {

	public MugglWsRsClient(SymbolicVirtualMachine vm) throws MugglWsRsException {
		super("javax.ws.rs.client.Client", vm);
	}
}





//public class MugglWsRsClient implements ReferenceValue {
//	
//	private static int globalInitNumber = 0;
//	
//	private int initNumber;
//	private InitializedClass initializedClass;
//	
//	public MugglWsRsClient(SymbolicVirtualMachine vm) throws MugglWsRsException {
//		this.initNumber = globalInitNumber++;
//		try {
//			ClassFile cf = vm.getClassLoader().getClassAsClassFile("javax.ws.rs.client.Client");
//			this.initializedClass = new InitializedClass(cf, vm);
//		} catch (ClassFileException e) {
//			throw new MugglWsRsException("Cannot initialize Muggl JAX-RS Client, since class javax.ws.rs.client.Client cannot be found. Please check class path.", e);
//		}
//	}
//	
//	@Override
//	public String toString() {
//		return "Muggl JAX-RS Client (initNumber="+this.initNumber+")";
//	}
//
//	@Override
//	public boolean isArray() {
//		return false;
//	}
//
//	@Override
//	public String getName() {
//		return "Muggl JAX-RS Client";
//	}
//
//	@Override
//	public String getSignature() {
//		return this.getClass().getName();
//	}
//
//	@Override
//	public InitializedClass getInitializedClass() {
//		return this.initializedClass;
//	}
//
//	@Override
//	public boolean isPrimitive() {
//		return false;
//	}
//
//	@Override
//	public long getInstantiationNumber() {
//		return initNumber;
//	}
//
//	@Override
//	public ReferenceValue clone() throws CloneNotSupportedException {
//		throw new CloneNotSupportedException();
//	}
//
//}
