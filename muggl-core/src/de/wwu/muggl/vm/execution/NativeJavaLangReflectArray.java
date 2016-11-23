package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;

public class NativeJavaLangReflectArray implements NativeMethodProvider {
	public static String pkg = "java.lang.reflect.Array";

	public static Arrayref newArray(Frame frame, Objectref compType, Integer dim) {

		ReferenceValue ref = null;
		// find out whether the compType needs a primitive wrapper
		if (compType.asClass().isPrimitive()) {
			// name field should be set, then
			String name = "";
			try {
				name = frame.getVm().getStringCache().getStringFieldValue(compType, "name");
				BasicType t = VmSymbols.name2type(name);
				ref = frame.getVm().getClassLoader().getClassAsClassFile(VmSymbols.PRIMITIVES_JAVA_CLASSES[t.value])
						.getAPrimitiveWrapperObjectref(frame.getVm());
			} catch (ExecutionException | ClassFileException e) {
				e.printStackTrace();
			}
		} else {
			ref = compType.getMirrorMuggl().getInitializedClass().getANewInstance();
		}
		return new Arrayref(ref, dim);
	}

	public void registerNatives() {
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "newArray",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class, Integer.class));
	}

}
