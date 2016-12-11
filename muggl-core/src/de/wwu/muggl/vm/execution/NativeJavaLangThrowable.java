package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * Implementation of native methods java.lang.Throwable
 * 
 * @author Max Schulze
 *
 */
public class NativeJavaLangThrowable extends NativeMethodProvider {
	public static String pkg = "java.lang.Throwable";

	// public static int getStackTraceElement(Frame frame, Objectref invokingObjectref, Integer index) {
	// return java_lang_Throwable.get_stack_trace_element(invokingObjectref, index);
	// }
	//
	// public static int getStackTraceDepth(Frame frame, Objectref invokingObjectref) {
	// return java_lang_Throwable.get_stack_trace_depth(frame, invokingObjectref);
	// }
	//
	public static Objectref fillInStackTrace(Frame frame, Objectref invokingObjectref, Integer index) {
		frame.getVm().fillDebugStackTraces();
		// java_lang_Throwable.fill_in_stack_trace(frame, invokingObjectref);
		return invokingObjectref;
	}

	public static void registerNatives() {
		NativeWrapper.registerNativeMethod(NativeJavaLangThrowable.class, pkg, "fillInStackTrace",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class, Integer.class),
				MethodType.methodType(Throwable.class, int.class));
		//
		// NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getStackTraceDepth",
		// MethodType.methodType(int.class, Frame.class, Objectref.class));
		// NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getStackTraceElement",
		// MethodType.methodType(int.class, Frame.class, Objectref.class, Integer.class));

	}

}
