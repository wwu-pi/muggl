package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;
import java.security.PrivilegedAction;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.Objectref;

public class NativeJavaSecurityAccessController extends NativeMethodProvider {
	public static String pkg = "java.security.AccessController";

	public static void doPrivileged(Frame frame, Objectref privilegedAction) {		
		// TODO: doPrivileged this could better be replaced by a blocking call to invokestatic
		
		ClassFile objClassfile = privilegedAction.getInitializedClass().getClassFile();
		Method m = objClassfile.getMethodByNameAndDescriptorOrNull(VmSymbols.RUN_METHOD_NAME, VmSymbols.VOID_OBJECT_SIGNATURE);
		
		frame.setPc(frame.getVm().getPc() + 1 +2);				
		frame.getVm().getStack().push(frame);

		// Push new one.
		try {
			frame.getVm().createAndPushFrame(frame, m, new Object[]{privilegedAction});
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		// Finish.
		frame.getVm().setReturnFromCurrentExecution(true);
	}

	public static void registerNatives() {
		NativeWrapper.registerNativeMethod(NativeJavaSecurityAccessController.class, pkg, "registerNatives",
				MethodType.methodType(void.class), MethodType.methodType(void.class));


		NativeWrapper.registerNativeMethod(NativeJavaSecurityAccessController.class, pkg, "doPrivileged",
				MethodType.methodType(void.class, Frame.class, Objectref.class),
				MethodType.methodType(Object.class, PrivilegedAction.class));
	}

}
