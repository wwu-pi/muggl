package de.wwu.muggl.javaee.ws;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public class MugglWsRsTarget extends MugglWsRs {

	/**
	 * The target URL of the JAX-RS web service.
	 */
	protected String targetUrl;
	
	/**
	 * A path to the target.
	 */
	protected String path;
	
	/**
	 * The template map for a path with path-parameters.
	 * For instance, path is /customer/{id}, then id is a path parameter
	 * and this map can set id = 123...
	 */
	protected Map<String, Object> templateMap;
	
	/**
	 * The Muggl JAX-RS Client that created this target object.
	 */
	protected MugglWsRsClient mugglWsClient;
	
	public MugglWsRsTarget(MugglWsRsTarget original) throws MugglWsRsException {
		this(original.vm, original.mugglWsClient);
		this.targetUrl = original.targetUrl;
		for(String k : original.templateMap.keySet()) {
			this.templateMap.put(k, original.templateMap.get(k));
		}
	}
	
	public MugglWsRsTarget(SymbolicVirtualMachine vm, MugglWsRsClient mugglWsClient) throws MugglWsRsException {
		super("javax.ws.rs.client.WebTarget", vm);
		this.mugglWsClient = mugglWsClient;
		this.templateMap = new HashMap<>();
	}
	
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setTemplate(String name, Object value) {
		this.templateMap.put(name, value);
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public String getPath() {
		return path;
	}

	public Map<String, Object> getTemplateMap() {
		return templateMap;
	}
}
