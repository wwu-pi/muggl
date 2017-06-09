package de.wwu.muggl.javaee.ws;

import java.util.HashSet;
import java.util.Set;

import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public class MugglWsRsInvocationBuilder extends MugglWsRs {

	/**
	 * The Muggl JAX-RS Target that created this builder.
	 */
	protected MugglWsRsTarget target;
	
	/**
	 * The accepted response types as a set of strings.
	 */
	protected Set<String> acceptedResponseTypes;
	
	public MugglWsRsInvocationBuilder(SymbolicVirtualMachine vm, MugglWsRsTarget target) throws MugglWsRsException {
		super("javax.ws.rs.client.Invocation$Builder", vm);
		this.acceptedResponseTypes = new HashSet<>();
	}

	public void addAcceptedResponseType(String acceptedResponseType) {
		this.acceptedResponseTypes.add(acceptedResponseType);
	}

}
