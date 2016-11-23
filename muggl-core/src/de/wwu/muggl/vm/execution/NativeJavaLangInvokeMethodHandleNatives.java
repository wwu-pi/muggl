package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SystemDictionary;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.VmSymbols.MethodHandleNativeConst;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileConstants;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;

public class NativeJavaLangInvokeMethodHandleNatives implements NativeMethodProvider {
	public static String pkg = "java.lang.invoke.MethodHandleNatives";

	public static Objectref resolve(Frame frame, Objectref invokingObjectref, Objectref mname, Objectref caller) {

		if (mname.getSysfields().get("vmtarget") != null) {
			// already resolved.
			return null;
		}

		return mname;
	}

	public static Object getMemberVMInfo(Frame frame, Objectref invokingObjectref, Object p1) {

		Objectref self = (Objectref) p1;
		Arrayref ret = new Arrayref(SystemDictionary.gI().Object_klass.getANewInstance(), 2);
		ret.putElement(0, self.getSysfields().getOrDefault("vmtarget", Integer.valueOf(0).longValue()));
		Objectref vmtarget = (Objectref) self.getSysfields().get("vmtarget");
		Objectref x = null;
		if (vmtarget == null) {
			x = null;
		} else if (vmtarget.getMirrorJava() != null) { // is_klass
			x = vmtarget.getMirrorJava();
		}
		ret.putElement(1, x);
		// TODO FIXME mxs: isMethod?
		/*
		 * else if(vmtarget.is)
		 * 
		 */
		return ret;
	}

	public static Object init(Frame frame, Objectref invokingObjectref, Objectref self, Objectref ref)
			throws VmRuntimeException {
		if (ref.getInitializedClass() == SystemDictionary.gI().reflect_Field_klass) {
			throw new VmRuntimeException(
					frame.getVm().generateExc("java.lang.NullPointerException", "init for fields not currently impl"));
		} else if (ref.getInitializedClass() == SystemDictionary.gI().reflect_Method_klass) {
			String methodName = "";
			String methodSig = "";
			try {
				methodName = frame.getVm().getStringCache().getStringFieldValue(ref, "name");
				methodSig = frame.getVm().getStringCache().getStringFieldValue(ref, "signature");
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			Objectref MugglClass = (Objectref) ref
					.getField(ref.getInitializedClass().getClassFile().getFieldByName("clazz"));
			ClassFile cf = MugglClass.getMirrorMuggl();

			Method m = cf.getMethodByNameAndDescriptorOrNull(methodName, methodSig);
			if (m == null || m.isSignaturePolymorphic()) {
				frame.getOperandStack().push(null);
				return true;
			}
			// very harsh shortcut, maybe we should compute more things?
			self.putField(self.getInitializedClass().getClassFile().getFieldByName("clazz"),
					m.getClassFile().getMirrorJava());

			// compute flags
			int flags = m.getAccessFlags() & VmSymbols.RECOGNIZED_METHOD_MODIFIERS;

			if (m.getClassFile().isAccInterface()) {
				flags |= MethodHandleNativeConst.MN_IS_METHOD.value
						| (ClassFileConstants.ReferenceKind.REF_invokeInterface
								.getReferenceKindIdx() << MethodHandleNativeConst.MN_REFERENCE_KIND_SHIFT.value);
			} else if (m.isAccStatic()) {
				flags |= MethodHandleNativeConst.MN_IS_METHOD.value | (ClassFileConstants.ReferenceKind.REF_invokeStatic
						.getReferenceKindIdx() << MethodHandleNativeConst.MN_REFERENCE_KIND_SHIFT.value);
			} else if (m.isInitializer()) {
				flags |= MethodHandleNativeConst.MN_IS_CONSTRUCTOR.value
						| (ClassFileConstants.ReferenceKind.REF_invokeSpecial
								.getReferenceKindIdx() << MethodHandleNativeConst.MN_REFERENCE_KIND_SHIFT.value);
			} else if (m.isAccPublic() || m.isAccProtected()) {
				flags |= MethodHandleNativeConst.MN_IS_METHOD.value
						| (ClassFileConstants.ReferenceKind.REF_invokeVirtual
								.getReferenceKindIdx() << MethodHandleNativeConst.MN_REFERENCE_KIND_SHIFT.value);
			} else {
				flags |= MethodHandleNativeConst.MN_IS_METHOD.value
						| (ClassFileConstants.ReferenceKind.REF_invokeSpecial
								.getReferenceKindIdx() << MethodHandleNativeConst.MN_REFERENCE_KIND_SHIFT.value);
			}

			self.putField(self.getInitializedClass().getClassFile().getFieldByName("flags"), flags);
		} else if (ref.getInitializedClass() == SystemDictionary.gI().reflect_Constructor_klass) {

		}

		return true;
	}

	public void registerNatives() {
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getMemberVMInfo",
				MethodType.methodType(Object.class, Frame.class, Objectref.class, Object.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "init",
				MethodType.methodType(Object.class, Frame.class, Objectref.class, Objectref.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "resolve",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class, Objectref.class, Objectref.class));

	}

}
