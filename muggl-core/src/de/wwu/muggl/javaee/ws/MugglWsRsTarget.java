package de.wwu.muggl.javaee.ws;

import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public class MugglWsRsTarget extends MugglWsRs {

	/**
	 * The target URL of the JAX-RS web service.
	 */
	protected String targetUrl;
	
	/**
	 * The Muggl JAX-RS Client that created this target object.
	 */
	protected MugglWsRsClient mugglWsClient;
	
	public MugglWsRsTarget(SymbolicVirtualMachine vm, MugglWsRsClient mugglWsClient) throws MugglWsRsException {
		super("javax.ws.rs.client.WebTarget", vm);
		this.mugglWsClient = mugglWsClient;
	}
	
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

}
