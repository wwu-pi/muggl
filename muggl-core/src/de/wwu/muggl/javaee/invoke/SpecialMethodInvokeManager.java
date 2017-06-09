package de.wwu.muggl.javaee.invoke;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.javaee.invoke.impl.ClassGetComponentType;
import de.wwu.muggl.javaee.invoke.impl.MugglEntityManagerFind;
import de.wwu.muggl.javaee.invoke.impl.ObjectGetClass;
import de.wwu.muggl.javaee.invoke.impl.WsRsClientBuilderNewClient;
import de.wwu.muggl.javaee.invoke.impl.WsRsClientEntity;
import de.wwu.muggl.javaee.invoke.impl.WsRsClientTarget;
import de.wwu.muggl.javaee.invoke.impl.WsRsInvocationBuilderPost;
import de.wwu.muggl.javaee.invoke.impl.WsRsResponseGetStatus;
import de.wwu.muggl.javaee.invoke.impl.WsRsTargetRequest;

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
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("javax.persistence.EntityManager", "find", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;"),
				new MugglEntityManagerFind());
		
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("javax.ws.rs.client.ClientBuilder", "newClient", "()Ljavax/ws/rs/client/Client;"),
				new WsRsClientBuilderNewClient());
		
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("javax.ws.rs.client.Client", "target", "(Ljava/lang/String;)Ljavax/ws/rs/client/WebTarget;"),
				new WsRsClientTarget());
		
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("javax.ws.rs.client.WebTarget", "request", "([Ljava/lang/String;)Ljavax/ws/rs/client/Invocation$Builder;"),
				new WsRsTargetRequest());
		
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("javax.ws.rs.client.Entity", "entity", "(Ljava/lang/Object;Ljava/lang/String;)Ljavax/ws/rs/client/Entity;"),
				new WsRsClientEntity());
		
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("javax.ws.rs.client.Invocation$Builder", "post", "(Ljavax/ws/rs/client/Entity;Ljava/lang/Class;)Ljava/lang/Object;"),
				new WsRsInvocationBuilderPost());
		
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("javax.ws.rs.core.Response", "getStatus", "()I"),
				new WsRsResponseGetStatus());
		
		
		
		
		
		
		
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("java.lang.Class", "getComponentType", "()Ljava/lang/Class;"),
				new ClassGetComponentType());
		
		this.specialMethods.put(
				new SpecialMethodInvocationEntry("java.lang.Object", "getClass", "()Ljava/lang/Class;"),
				new ObjectGetClass());
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
