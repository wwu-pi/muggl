package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.JavaClasses.java_lang_Class;
import de.wwu.muggl.vm.JavaClasses.java_lang_boxing_object;
import de.wwu.muggl.vm.JavaClasses.java_lang_invoke_MemberName;
import de.wwu.muggl.vm.JavaClasses.java_lang_invoke_MethodType;
import de.wwu.muggl.vm.SystemDictionary;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileConstants;
import de.wwu.muggl.vm.classfile.ClassFileConstants.ReferenceKind;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ObjectrefAsClass;

public class NativeJavaLangInvokeMethodHandleNatives extends NativeMethodProvider {

	// decoding CONSTANT_MethodHandle constants
	static final int JVM_REF_MIN = ClassFileConstants.ReferenceKind.REF_getField.val(),
			JVM_REF_MAX = ClassFileConstants.ReferenceKind.REF_invokeInterface.val();

	static final int ALL_KINDS = VmSymbols.MN_IS_METHOD | VmSymbols.MN_IS_CONSTRUCTOR | VmSymbols.MN_IS_FIELD
			| VmSymbols.MN_IS_TYPE;

	static boolean ref_kind_is_valid(int ref_kind) {
		return (ref_kind >= JVM_REF_MIN && ref_kind <= JVM_REF_MAX);
	}

	static boolean ref_kind_is_field(int ref_kind) {
		assert (ref_kind_is_valid(ref_kind));
		return (ref_kind <= ClassFileConstants.ReferenceKind.REF_putStatic.val());
	}

	static boolean ref_kind_is_getter(int ref_kind) {
		assert (ref_kind_is_valid(ref_kind));
		return (ref_kind <= ClassFileConstants.ReferenceKind.REF_getStatic.val());
	}

	static boolean ref_kind_is_setter(int ref_kind) {
		return ref_kind_is_field(ref_kind) && !ref_kind_is_getter(ref_kind);
	}

	static boolean ref_kind_is_method(int ref_kind) {
		return !ref_kind_is_field(ref_kind)
				&& (ref_kind != ClassFileConstants.ReferenceKind.REF_newInvokeSpecial.val());
	}

	static boolean ref_kind_has_receiver(int ref_kind) {
		assert (ref_kind_is_valid(ref_kind));
		return (ref_kind & 1) != 0;
	}

	static boolean ref_kind_is_static(int ref_kind) {
		return !ref_kind_has_receiver(ref_kind)
				&& (ref_kind != ClassFileConstants.ReferenceKind.REF_newInvokeSpecial.val());
	}

	static boolean ref_kind_does_dispatch(int ref_kind) {
		return (ref_kind == ClassFileConstants.ReferenceKind.REF_invokeVirtual.val()
				|| ref_kind == ClassFileConstants.ReferenceKind.REF_invokeInterface.val());
	}

	public static String pkg = "java.lang.invoke.MethodHandleNatives";

	// convert the external string or reflective type to an internal signature
	private static String lookup_signature(Frame frame, Objectref type, boolean internIfNotFound) {
		if (java_lang_invoke_MethodType.is_instance(frame, type)) {
			return java_lang_invoke_MethodType.as_signature(type, internIfNotFound);
		} else if (java_lang_Class.is_instance(type)) {
			return java_lang_Class.as_signature(type, false);
		}
		// else if (java_lang_String.is_instance(type)) {
		// if (internIfNotFound) {
		// return java_lang_String.as_symbol(type);
		// } else {
		// return java_lang_String.as_symbol_or_null(type);
		// }
		else {
			frame.getVm().generateExc("java.lang.InternalError", "unrecognized type");
			return null;
		}
	}

	public static Objectref resolve(Frame frame, Objectref mname, Objectref caller) {

		if (java_lang_invoke_MemberName.get_vmtarget(mname) != null) {
			// already resolved.
			return null;
		}

		Objectref defc = java_lang_invoke_MemberName.clazz(mname);
		Objectref name = java_lang_invoke_MemberName.get_name(mname);
		Objectref type = java_lang_invoke_MemberName.get_type(mname);
		int flags = java_lang_invoke_MemberName.get_flags(mname);

		int ref_kind = (flags >> VmSymbols.MN_REFERENCE_KIND_SHIFT) & VmSymbols.MN_REFERENCE_KIND_MASK;

		if (!ref_kind_is_valid(ref_kind)) {
			throw new java.lang.InternalError("obsolete MemberName format");
		}

		if (defc == null || name == null || type == null) {
			frame.getVm().generateExc("java.lang.IllegalArgumentException",
					"nothing to resolve " + ((defc == null) ? "defc null" : "") + ((name == null) ? "name null" : "")
							+ ((type == null) ? "type null" : ""));
			return null;
		}

		ObjectrefAsClass defc_klass = defc.asClass();
		ClassFile defcCF = null;

		if (defc_klass == null)
			return null; // primitive
		if (!defc_klass.isInstance()) {
			if (!defc_klass.isArray())
				return null;
			defcCF = SystemDictionary.gI().Object_klass.getClassFile();
		} else {
			defcCF = defc.getMirrorMuggl();
		}
		if (defcCF == null) {
			frame.getVm().generateExc("java.lang.InternalError", "primitive class");
		}
		defcCF.linkClass();

		String nameStr = frame.getVm().getStringCache().getStringObjrefValue(name);
		if (nameStr.isEmpty())
			return null;
		if (nameStr.equals(VmSymbols.CLASS_INITIALIZER_NAME))
			return null; // illegal

		// FIXME: much to fix in resolve here:
		// vmIntrinsics::ID mh_invoke_id = vmIntrinsics::_none;
		if ((flags & ALL_KINDS) == VmSymbols.MN_IS_METHOD &&
		// (defc() == SystemDictionary::MethodHandle_klass()) &&
				(ref_kind == ClassFileConstants.ReferenceKind.REF_invokeVirtual.val()
						|| ref_kind == ClassFileConstants.ReferenceKind.REF_invokeSpecial.val() ||
						// static invocation mode is required for _linkToVirtual, etc.:
						ref_kind == ClassFileConstants.ReferenceKind.REF_invokeStatic.val())) {

			// vmIntrinsics::ID iid = signature_polymorphic_name_id(name);
			// if (iid != vmIntrinsics::_none &&
			// ((ref_kind == JVM_REF_invokeStatic) == is_signature_polymorphic_static(iid))) {
			// // Virtual methods invoke and invokeExact, plus internal invokers like _invokeBasic.
			// // For a static reference it could an internal linkage routine like _linkToVirtual, etc.
			// mh_invoke_id = iid;
			// }
		}

		String typeStr = lookup_signature(frame, type, true);
		if (typeStr.isEmpty())
			return null;// no such signature exists in the VM

		// Time to do the lookup.
		switch (flags & ALL_KINDS) {
		case VmSymbols.MN_IS_METHOD:
			CallInfo result = null;
			// {
			if (ref_kind == ClassFileConstants.ReferenceKind.REF_invokeStatic.val()) {
				Globals.getInst().execLogger.trace("resolve asked to resolve invokestatic method " + name + ":"
						+ java_lang_invoke_MethodType.as_signature(type, false));
				result = LinkResolver.resolve_static_call(frame, defcCF, name, type, caller, caller != null, false);
			} else if (ref_kind == ClassFileConstants.ReferenceKind.REF_invokeInterface.val()) {
				// LinkResolver::resolve_interface_call(result, Handle(), defc,
				// defc, name, type, caller, caller.not_null(), false, THREAD);
				// } else if (mh_invoke_id != vmIntrinsics::_none) {
				// assert(!is_signature_polymorphic_static(mh_invoke_id), "");
				// LinkResolver::resolve_handle_call(result,
				// defc, name, type, caller, THREAD);
				Globals.getInst().execLogger.trace("not impl");
			} else if (ref_kind == ClassFileConstants.ReferenceKind.REF_invokeSpecial.val()) {
				// LinkResolver::resolve_special_call(result,
				// defc, name, type, caller, caller.not_null(), THREAD);
				Globals.getInst().execLogger.trace("not impl");
			} else if (ref_kind == ClassFileConstants.ReferenceKind.REF_invokeVirtual.val()) {
				Globals.getInst().execLogger.trace("resolve asked to resolve invokestatic method " + name + ":"
						+ java_lang_invoke_MethodType.as_signature(type, false));
				result = LinkResolver.resolve_virtual_call(frame, defcCF, defcCF, name, type, caller, caller != null,
						false);
			} else {
				// assert(false, err_msg("ref_kind=%d", ref_kind));
			}
			// if (HAS_PENDING
			// }
			// if (result.resolved_appendix().not_null()) {
			// // The resolved MemberName must not be accompanied by an appendix argument,
			// // since there is no way to bind this value into the MemberName.
			// // Caller is responsible to prevent this from happening.
			// THROW_MSG_(vmSymbols::java_lang_InternalError(), "appendix", empty);
			// }
			Objectref mname2 = init_method_MemberName(mname, result);
			return mname2;
		case VmSymbols.MN_IS_CONSTRUCTOR:
			// CallInfo result;
			// {
			// assert(!HAS_PENDING_EXCEPTION, "");
			// if (name == vmSymbols::object_initializer_name()) {
			// LinkResolver::resolve_special_call(result,
			// defc, name, type, caller, caller.not_null(), THREAD);
			// } else {
			// break; // will throw after end of switch
			// }
			// if (HAS_PENDING_EXCEPTION) {
			// return empty;
			// }
			// }
			// assert(result.is_statically_bound(), "");
			// oop mname2 = init_method_MemberName(mname, result);
			// return Handle(THREAD, mname2);
			Globals.getInst().execLogger.warn("mxs: warning resolve for constructor not implemented");
			break;
		case VmSymbols.MN_IS_FIELD:
			Field res; // find_field initializes fd if found
			res = LinkResolver.resolve_field(frame, defcCF, name, type, caller, Bytecodes.nop, false, false);
			mname2 = init_field_MemberName(frame, mname, res, ref_kind_is_setter(ref_kind));
			return mname2;
		default:
			frame.getVm().generateExc("java.lang.InternalError", "unrecognized MemberName format");
			return null;
		}
		frame.getVm().fillDebugStackTraces();
		return null;
	}

	private static Objectref init_field_MemberName(Frame frame, Objectref mname, Field res, boolean is_setter) {
		int flags = res.getAccessFlags() & Modifier.fieldModifiers();

		flags |= VmSymbols.MN_IS_FIELD | ((res.isAccStatic() ? ClassFileConstants.ReferenceKind.REF_getStatic.val()
				: ClassFileConstants.ReferenceKind.REF_getField.val()) << VmSymbols.MN_REFERENCE_KIND_SHIFT);
		if (is_setter)
			flags += ((ClassFileConstants.ReferenceKind.REF_putField.val()
					- ClassFileConstants.ReferenceKind.REF_getField.val()) << VmSymbols.MN_REFERENCE_KIND_SHIFT);
		int vmindex = 1; // ?? // determines the field uniquely when combined with static bit

		java_lang_invoke_MemberName.set_flags(mname, flags);
		java_lang_invoke_MemberName.set_vmtarget(mname, res.getClassFile());
		java_lang_invoke_MemberName.set_vmindex(mname, vmindex);
		java_lang_invoke_MemberName.set_clazz(mname, res.getClassFile().getMirrorJava());

		Objectref type = frame.getVm().getStringCache().getStringObjectref(res.getDescriptor());
		Objectref name = frame.getVm().getStringCache().getStringObjectref(res.getName());
		if (name != null)
			java_lang_invoke_MemberName.set_name(mname, name);
		if (type != null)
			java_lang_invoke_MemberName.set_type(mname, type);
		// Note: name and type can be lazily computed by resolve_MemberName,
		// if Java code needs them as resolved String and Class objects.
		// Note that the incoming type oop might be pre-resolved (non-null).
		// The base clazz and field offset (vmindex) must be eagerly stored,
		// because they unambiguously identify the field.
		// Although the fieldDescriptor::_index would also identify the field,
		// we do not use it, because it is harder to decode.
		// TO DO: maybe intern mname_oop
		return mname;
	}

	private static Objectref init_method_MemberName(Objectref mname, CallInfo info) {
		assert (info.get_resolved_appendix() == null) : "only normal methods here";

		Method method = info.get_resolved_method();
		ClassFile m_klass = method.getClassFile();
		int flags = method.getAccessFlags() & Modifier.methodModifiers();
		int vmindex = Method.INVALID_VTABLE_INDEX;

		switch (info.get_call_kind()) {
		case ITABLE_CALL:
			vmindex = info.itable_index();
			// More importantly, the itable index only works with the method holder.
			// assert(m_klass->veriCfy_itable_index(vmindex), "");
			flags |= VmSymbols.MN_IS_METHOD
					| (ReferenceKind.REF_invokeInterface.val() << VmSymbols.MN_REFERENCE_KIND_SHIFT);

			break;

		case VTABLE_CALL:
			vmindex = info.vtable_index();
			flags |= VmSymbols.MN_IS_METHOD
					| (ReferenceKind.REF_invokeVirtual.val() << VmSymbols.MN_REFERENCE_KIND_SHIFT);
			// assert(info.resolved_klass()->is_subtype_of(m_klass()), "virtual call must be type-safe");
			if (m_klass.isAccInterface()) {
				// This is a vtable call to an interface method (abstract "miranda method" or default method).
				// The vtable index is meaningless without a class (not interface) receiver type, so get one.
				// (LinkResolver should help us figure this out.)
				ClassFile m_klass_non_interface = info.get_resolved_klass();
				if (m_klass_non_interface.isAccInterface()) {
					m_klass_non_interface = SystemDictionary.gI().Object_klass.getClassFile();

				}
				if (!method.isAccPublic()) {
					assert (method.isAccPublic()) : "virtual call must be to public interface method";
					return null; // elicit an error later in product build
				}
				// assert(info.resolved_klass()->is_subtype_of(m_klass_non_interface()), "virtual call must be
				// type-safe");
				m_klass = m_klass_non_interface;
			}

			break;

		case DIRECT_CALL:
			vmindex = Method.NONVIRTUAL_VTABLE_INDEX;
			if (method.isAccStatic()) {
				flags |= VmSymbols.MN_IS_METHOD
						| (ReferenceKind.REF_invokeStatic.val() << VmSymbols.MN_REFERENCE_KIND_SHIFT);
			} else if (method.isInitializer()) {
				flags |= VmSymbols.MN_IS_CONSTRUCTOR
						| (ReferenceKind.REF_invokeSpecial.val() << VmSymbols.MN_REFERENCE_KIND_SHIFT);
			} else {
				flags |= VmSymbols.MN_IS_METHOD
						| (ReferenceKind.REF_invokeSpecial.val() << VmSymbols.MN_REFERENCE_KIND_SHIFT);
			}
			break;

		default:
			assert (false) : "bad CallInfo";
			return null;
		}

		// @CallerSensitive annotation detected
		if (method.isCallerSensitive()) {
			flags |= VmSymbols.MN_CALLER_SENSITIVE;
		}

		java_lang_invoke_MemberName.set_flags(mname, flags);
		java_lang_invoke_MemberName.set_vmtarget(mname, method);
		java_lang_invoke_MemberName.set_vmindex(mname, vmindex); // vtable/itable index
		java_lang_invoke_MemberName.set_clazz(mname, m_klass.getMirrorJava());
		// Note: name and type can be lazily computed by resolve_MemberName,
		// if Java code needs them as resolved String and MethodType objects.
		// The clazz must be eagerly stored, because it provides a GC
		// root to help keep alive the Method*.
		// If relevant, the vtable or itable value is stored as vmindex.
		// This is done eagerly, since it is readily available without
		// constructing any new objects.
		// TO DO: maybe intern mname_oop

		// was soll das?
		// m->method_holder()->add_member_name(m->method_idnum(), mname);

		return mname;
	}

	/**
	 * Always return nothing, leave the box alone so verifyConstants will succeed
	 * 
	 * @param which
	 * @param name
	 * @return
	 */
	public static int getNamedCon(Frame frame, Integer which, Arrayref box) {
		return -1;
	}

	public static Object getMemberVMInfo(Frame frame, Object p1) {
		Objectref self = (Objectref) p1;
		if (self == null)
			return null;
		Integer vmindex = java_lang_invoke_MemberName.get_vmindex(self);
		Object vmtarget = java_lang_invoke_MemberName.get_vmtarget(self);
		Arrayref ret = new Arrayref(SystemDictionary.gI().Object_klass.getANewInstance(), 2);

		ret.putElement(0, java_lang_boxing_object.create(BasicType.T_LONG, vmindex.longValue()));

		Objectref x = null;
		if (vmtarget == null) {
			x = null;
		} else if (vmtarget instanceof ClassFile) { // is_klass
			x = ((ClassFile) vmtarget).getMirrorJava();
		} else if (vmtarget instanceof Method) {
			x = self;
		}
		ret.putElement(1, x);
		return ret;
	}

	public static void init(Frame frame, Objectref self, Objectref ref) throws VmRuntimeException {
		frame.getVm().fillDebugStackTraces();
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

			Objectref MugglClass = java_lang_invoke_MemberName.clazz(ref);
			ClassFile cf = MugglClass.getMirrorMuggl();

			Method m = cf.getMethodByNameAndDescriptorOrNull(methodName, methodSig);
			if (m == null || m.isSignaturePolymorphic()) {
				return;
			}

			CallInfo info = new CallInfo(m, m.getClassFile());
			init_method_MemberName(self, info);

		} else if (ref.getInitializedClass() == SystemDictionary.gI().reflect_Constructor_klass) {

		}

	}

	public static long staticFieldOffset(Frame frame, Objectref self) {
		return 3L; // fake
	}

	public static long objectFieldOffset(Frame frame, Objectref self) {
		return 4L; // fake
	}

	public static Objectref staticFieldBase(Frame frame, Objectref self) {
		return (Objectref) java_lang_invoke_MemberName.get_vmtarget(self);
	}

	@SuppressWarnings("deprecation")
	// most of the deprecation warnings can be explained by the fact that java.lang.invoke.MemberName is not visible
	public static void registerNatives() {
		NativeWrapper.registerNativeMethod(NativeJavaLangInvokeMethodHandleNatives.class, pkg, "registerNatives",
				MethodType.methodType(void.class), MethodType.methodType(void.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangInvokeMethodHandleNatives.class, pkg, "getMemberVMInfo",
				MethodType.methodType(Object.class, Frame.class, Object.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangInvokeMethodHandleNatives.class, pkg, "init",
				MethodType.methodType(void.class, Frame.class, Objectref.class, Objectref.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangInvokeMethodHandleNatives.class, pkg, "resolve",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class, Objectref.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangInvokeMethodHandleNatives.class, pkg, "getNamedCon",
				MethodType.methodType(int.class, Frame.class, Integer.class, Arrayref.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangInvokeMethodHandleNatives.class, pkg, "staticFieldOffset",
				MethodType.methodType(long.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangInvokeMethodHandleNatives.class, pkg, "objectFieldOffset",
				MethodType.methodType(long.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangInvokeMethodHandleNatives.class, pkg, "staticFieldBase",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class));

	}

}
