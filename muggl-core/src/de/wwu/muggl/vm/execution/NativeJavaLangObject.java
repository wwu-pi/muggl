package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;
import java.util.Optional;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.FreeObjectref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;

public class NativeJavaLangObject extends NativeMethodProvider {
	public static String pkg = "java.lang.Object";

	public static void notifyAll(Frame frame, Objectref invokingObjectref) {
		// TODO monitor implementation of notifyAll
		// fake here
		return;
	}

	public static void notify(Frame frame, Objectref invokingObjectref) {
		// TODO monitor implementation of notify
		// fake here
		return;
	}

	public static Objectref getClass(Frame frame, ReferenceValue invokingRefVal) {
        if (invokingRefVal instanceof Objectref) {
            return ((Objectref) invokingRefVal).getMirrorJava();
        } else if (invokingRefVal instanceof Arrayref) {
			Arrayref invokingArr = (Arrayref) invokingRefVal;
			return invokingArr.getMirrorJava();
		}
		return null;
	}

	public static int hashCode(Frame frame, ReferenceValue invokingRefVal) {
		// do not do toJava wrapping for hashCode, since it wouln't be the same Object again!
		Globals.getInst().execLogger.trace("for refval id: " + invokingRefVal.getInstantiationNumber()
				+ " computed hashcode:" + invokingRefVal.hashCode());
		return invokingRefVal.hashCode();
	}

	public static ReferenceValue clone(Frame frame, ReferenceValue invokingRefVal) {
		try {
			return invokingRefVal.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void registerNatives() {
		NativeWrapper.registerNativeMethod(NativeJavaLangObject.class, pkg, "registerNatives",
				MethodType.methodType(void.class), MethodType.methodType(void.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangObject.class, pkg, "clone",
				MethodType.methodType(ReferenceValue.class, Frame.class, ReferenceValue.class),
				MethodType.methodType(Object.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangObject.class, pkg, "getClass",
				MethodType.methodType(Objectref.class, Frame.class, ReferenceValue.class),
				MethodType.methodType(Class.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangObject.class, pkg, "hashCode",
				MethodType.methodType(int.class, Frame.class, ReferenceValue.class), MethodType.methodType(int.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangObject.class, pkg, "notifyAll",
				MethodType.methodType(void.class, Frame.class, Objectref.class), MethodType.methodType(void.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangObject.class, pkg, "notify",
				MethodType.methodType(void.class, Frame.class, Objectref.class), MethodType.methodType(void.class));
	}

}
