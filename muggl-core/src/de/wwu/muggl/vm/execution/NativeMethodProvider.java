package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;

import de.wwu.muggl.vm.Frame;

/**
 * Interface to group all classes that provide methods that are 'native' in the JVM
 * 
 * @author Max Schulze
 *
 */
public interface NativeMethodProvider {
	abstract void registerNatives();

	/**
	 * Example functions to place in classes implementing this interface. First two parameters are frame, and
	 * invokingObjectref if 'native' method is non-static
	 * 
	 * Then the following parameters in the Order they appear in the java source!
	 * 
	 * Don't forget to add the class' registerNatives in static - code region in NativeWrapper
	 * 
	 */

	/*
	 * public static void arraycopy(Frame frame, Object p0, Object p1, Object p2, Object p3, Object p4) {
	 * 
	 * ... blabla
	 * 
	 * }
	 */

	/*
	 * public void registerNatives() {
	 * 
	 * NativeWrapper.registerNativeMethod(this.getClass(), pkg, "arraycopy", MethodType.methodType(void.class,
	 * Frame.class, Object.class, Object.class, Object.class, Object.class, Object.class));
	 * 
	 * }
	 */

}
