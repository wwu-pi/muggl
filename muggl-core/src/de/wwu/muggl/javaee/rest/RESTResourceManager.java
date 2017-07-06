package de.wwu.muggl.javaee.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RESTResourceManager {

	protected static RESTResourceManager instance;
	protected Map<Integer, Set<RESTResource>> requiredRESTResourcesMap;
	
	public RESTResourceManager() {
		this.requiredRESTResourcesMap = new HashMap<>();
	}
	
	public void addRequiredResource(int constraintLevel, RESTResource resource) {
		Set<RESTResource> resourceSet = requiredRESTResourcesMap.get(constraintLevel);
		if(resourceSet == null) {
			resourceSet = new HashSet<>();
		}
		resourceSet.add(resource);		
		this.requiredRESTResourcesMap.put(constraintLevel, resourceSet);
	}
	
	/**
	 * Get required REST resources up to the given constraint level.
	 * @param constraintLevel the constraint level to up to which to the REST resources should be returned
	 * @return
	 */
	public Set<RESTResource> getRequiredRESTResources(int constraintLevel) {
		if(constraintLevel < 0) {
			return new HashSet<>();
		}
		Set<RESTResource> requiredResources = new HashSet<>();
		for(int i=constraintLevel; i>=0; i--) {
			Set<RESTResource> set = requiredRESTResourcesMap.get(i);
			if(set != null) {
				requiredResources.addAll(set);
			}
		}
		return requiredResources;
	}
	
	public static synchronized RESTResourceManager getInst() {
		if(instance == null) {
			instance = new RESTResourceManager();
		}
		return instance;
	}
}
