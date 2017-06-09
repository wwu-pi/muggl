package de.wwu.muggl.javaee.ws;

import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public class MugglWsRsEntity  extends MugglWsRs {

	public MugglWsRsEntity(SymbolicVirtualMachine vm) throws MugglWsRsException {
		super("javax.ws.rs.client.Entity", vm);
	}

}
