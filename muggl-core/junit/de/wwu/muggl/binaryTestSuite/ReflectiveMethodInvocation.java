package de.wwu.muggl.binaryTestSuite;

import java.lang.reflect.InvocationTargetException;

/**
 * "Simple" method invocation without MethodType, methodLookup, etc...
 * 
 * @author max
 *
 */
public class ReflectiveMethodInvocation {
	public static String StaticNoArgString() {
		return "hello, world!";
	}

	public static String METHOD_test_invokeMethod = "test_invokeMethod";

	public static String test_invokeMethod() {
		Object ret = null;
		try {
			ret = ReflectiveMethodInvocation.class.getDeclaredMethods()[0].invoke(null, new Object[0]);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			return "failed...";
		}
		return (String) ret;
	}

	public static void main(String[] args) {

	}
}
