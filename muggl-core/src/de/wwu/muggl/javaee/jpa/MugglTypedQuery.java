package de.wwu.muggl.javaee.jpa;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for javax.persistence.TypedQuery
 * 
 * @author Andreas Fuchs
 *
 */
public class MugglTypedQuery {

	/**
	 * The typed query string.
	 */
	protected String query;
	
	/**
	 * The type of this queries return value.
	 */
	protected String className;
	
	/**
	 * A parameter map.
	 */
	protected Map<String, Object> parameterMap;
	
	public MugglTypedQuery(String query, String className) {
		this.query = query;
		this.className = className;
		this.parameterMap = new HashMap<>();
	}
	
	public void setParameter(String name, Object value) {
		this.parameterMap.put(name, value);
	}
	
	
}
