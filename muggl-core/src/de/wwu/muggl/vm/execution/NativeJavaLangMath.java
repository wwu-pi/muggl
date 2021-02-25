package de.wwu.muggl.vm.execution;

import de.wwu.muggl.vm.Frame;

import java.lang.invoke.MethodType;

public class NativeJavaLangMath extends NativeMethodProvider {
	public static String pkg = "java.lang.Math";

	public static double random(Frame frame) {
		// Provide an unique String object reference.
		double i = Math.random();
		return i;
	}

	public static void registerNatives() {
		NativeWrapper.registerNativeMethod(NativeJavaLangMath.class, pkg, "random",
				MethodType.methodType(double.class, Frame.class),
				MethodType.methodType(double.class));
	}

}
