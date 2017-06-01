package de.wwu.muggl.javaee.invoke;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.javaee.invoke.impl.MugglEntityManagerFind;

/**
 * Manager for the special method invocations.
 * @author Andreas Fuchs
 */
public class SpecialMethodInvokeManager {

	// singleton instance
	protected static SpecialMethodInvokeManager instance;
	
	/**
	 * The special methods map.
	 */
	protected Map<SpecialMethodInvocationEntry, SpecialMethodInvocation> specialMethods;
	
	private SpecialMethodInvokeManager() {
		this.specialMethods = new HashMap<>();
		addSpecialMethods();
	}

	/**
	 * Get the special method invocation for the given class, 
	 * with the given method name, and the given method signature.  
	 * @param className the class name
	 * @param methodName the method name
	 * @param methodSignature the method signature
	 */
	public SpecialMethodInvocation getSpecialMethodInvocation(String className, String methodName, String methodSignature) {
		SpecialMethodInvocationEntry s = new SpecialMethodInvocationEntry(className, methodName, methodSignature);
		return this.specialMethods.get(s);
	}
	
	/**
	 * Add all special handling methods.
	 * Currently, this is static.
	 * This configuration could also be loaded via a configuration file.
	 * Feel free to implement!
	 */
	private void addSpecialMethods() {
		SpecialMethodInvocationEntry entityManagerFindEntry = new SpecialMethodInvocationEntry("javax.persistence.EntityManager", "find", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;");
		MugglEntityManagerFind findMethodInvocation = new MugglEntityManagerFind();
		this.specialMethods.put(entityManagerFindEntry, findMethodInvocation);
	}
	
	/**
	 * Check if the specified method from the given class, 
	 * with the given method name, 
	 * and the given method signature
	 * requires a special handling.
	 * @param className the class name
	 * @param methodName the method name
	 * @param methodSignature the method signature
	 * @return
	 */
	public boolean isSpecialMethod(String className, String methodName, String methodSignature) {
		SpecialMethodInvocationEntry s = new SpecialMethodInvocationEntry(className, methodName, methodSignature);
		return this.specialMethods.containsKey(s);
	}
	
	/**
	 * Get the singleton instance.
	 */
	public synchronized static SpecialMethodInvokeManager getInst() {
		if(instance == null) {
			instance = new SpecialMethodInvokeManager();
		}
		return instance;
	}
}
