package de.wwu.muggl.binaryTestSuite;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * "Simple" method invocation without MethodType, methodLookup, but only using reflection
 * 
 * @author max
 *
 */
public class ReflectiveMethodInvocation {
	public static String StaticNoArgString() {
		return "hello, world!";
	}

	public static String StaticArgString(int i) {
		return "hello, world!" + i;
	}

	public String InstArgString(int i) {
		return "hello, world!" + i;
	}

	public boolean retBoolean() {
		return true;
	}

	public static String METHOD_test_invokeMethod = "test_invokeMethod";

	public static String test_invokeMethod() {
		Object ret = null;
		try {
			Method m = ReflectiveMethodInvocation.class.getDeclaredMethods()[0];
			ret = m.invoke(null, new Object[0]);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			return "failed...";
		}
		return (String) ret;
	}

	public static boolean test_invokeMethodReturnPrimitive() {
		try {
			Method m = ReflectiveMethodInvocation.class.getDeclaredMethods()[3];
			return m.invoke(null, new Object[0]).getClass().isPrimitive();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String METHOD_test_invokeMethodWithArg = "test_invokeMethodWithArg";

	public static String test_invokeMethodWithArg() {
		Object ret = null;
		try {
			ret = ReflectiveMethodInvocation.class.getDeclaredMethods()[1].invoke(null, new Object[] { 2 });
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			return "failed...";
		}
		return (String) ret;
	}

	public static String METHOD_test_invokeInstanceMethodWithArg = "test_invokeInstanceMethodWithArg";

	public static String test_invokeInstanceMethodWithArg() {
		Object ret = null;
		ReflectiveMethodInvocation test = new ReflectiveMethodInvocation();
		try {
			ret = ReflectiveMethodInvocation.class.getDeclaredMethods()[2].invoke(test, new Object[] { 3 });
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			return "failed...";
		}
		return (String) ret;
	}

	public static void main(String[] args) {
		System.out.println(test_invokeMethodReturnPrimitive());
	}
}
