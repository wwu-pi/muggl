package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.initialization.Objectref;

public class NativeJavaLangString implements NativeMethodProvider {
	public static String pkg = "java.lang.String";

	public static Objectref intern(Frame frame, Objectref invokingObjectref) {
		// Provide an unique String object reference.
		Objectref stringObjectref = frame.getVm().getStringCache().getStringObjectref(invokingObjectref);
		return stringObjectref;
	}

	public void registerNatives() {
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "intern",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class));
	}

}
