package de.wwu.muggl.javaee.ws;

import java.util.HashSet;
import java.util.Set;

import de.wwu.muggl.javaee.rest.RESTResource;
import de.wwu.muggl.javaee.rest.RESTResourceManager;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public class MugglWsRsInvocationBuilder extends MugglWsRs {

	/**
	 * The Muggl JAX-RS Target that created this builder.
	 */
	protected MugglWsRsTarget target;
	
	/**
	 * The REST resource that is built by this invocation builder.
	 */
	protected RESTResource restResource;
	
	/**
	 * The accepted response types as a set of strings.
	 */
	protected Set<String> acceptedResponseTypes;
	
	public MugglWsRsInvocationBuilder(SymbolicVirtualMachine vm, MugglWsRsTarget target) throws MugglWsRsException {
		super("javax.ws.rs.client.Invocation$Builder", vm);
		this.acceptedResponseTypes = new HashSet<>();
		this.target = target;
		buildRESTResource();
	}

	private void buildRESTResource() {
		String endpoint = this.target.getTargetUrl();
		this.restResource = new RESTResource(endpoint);
		
		int constraintLevel = this.vm.getSolverManager().getConstraintLevel();
		RESTResourceManager.getInst().addRequiredResource(constraintLevel, this.restResource);
	}

	public void addAcceptedResponseType(String acceptedResponseType) {
		this.acceptedResponseTypes.add(acceptedResponseType);
	}
	
	public RESTResource getRESTResource() {
		return this.restResource;
	}

}
