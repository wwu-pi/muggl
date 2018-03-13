package de.wwu.muggl.vm;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.PrimitiveWrappingImpossibleException;
import de.wwu.muggl.vm.initialization.ReferenceValue;

/**
 * Provides methods to manipulate objectrefs from the Muggl-VM ("outside", not from within the (bytecode) execution)
 * equivalence of openjdk/jdk/src/share/native/java/lang/javaClasses
 * 
 * Intentionally breaking Java naming convention to easily find the methods in openjdk source
 * 
 * @author Max Schulze
 *
 */
public final class JavaClasses {

	public static class java_lang_thread {
		public static void set_thread(Objectref java_thread, Object Thread) {
			// java_thread->address_field_put(_eetop_offset, (address)thread);
			// There is no field in the java object were we could possibly store the reference.
			// In java, we can't get memory adresses of objects.
		}

		public static void set_priority(Objectref java_thread, int prio) {
			ClassFile methodClassFile = java_thread.getInitializedClass().getClassFile();
			Field field = methodClassFile.getFieldByName(VmSymbols.PRIORITY_NAME);
			java_thread.putField(field, prio);
		}

		public static void set_thread_status(Objectref java_thread, java.lang.Thread.State runnable) {
			ClassFile methodClassFile = java_thread.getInitializedClass().getClassFile();
			Field field = methodClassFile.getFieldByName(VmSymbols.THREADSTATUS_NAME);
			java_thread.putField(field, runnable.ordinal());
		}
	}

	public static class java_lang_Class {
		public static boolean is_instance(Objectref java_class) {
			return (java_class.getInitializedClass() != null)
					&& (java_class.getInitializedClass() == SystemDictionary.gI().Class_klass
							|| java_class.getInitializedClass().getClassFile().getName().equals("java.lang.Class"));
		}

		public static BasicType primitive_type(Objectref java_class) {
			for (BasicType bt : BasicType.values()) {
			    if (bt.value > BasicType.T_VOID.value)
			        break;
				if (Universe.java_mirror(bt) == java_class)
					return bt;
			}

			return BasicType.T_ILLEGAL;

		}

		public static String as_signature(Objectref java_class, boolean intern_if_not_found) {
			assert (is_instance(java_class)) : "must be a Class object";
			StringBuilder str = new StringBuilder();
			print_signature(java_class, str);
			return str.toString();

		}

		// do nothing. This is for C code.
		public static int oop_size(ReferenceValue java_class) {
			return -1;
		}

		public static void set_oop_size(ReferenceValue java_class, int size) {
		}

		public static int static_oop_field_count(ReferenceValue java_class) {
			return -1;
		}

		public static void set_static_oop_field_count(ReferenceValue java_class, int size) {

		}

		public static Objectref createBasicTypeMirror(String name, BasicType type) {

			Objectref java_class = SystemDictionary.gI().getVm()
					.getAndInitializeObjectref(SystemDictionary.gI().Class_klass);
			java_class.putField(java_class.getInitializedClass().getClassFile().getFieldByName("name"),
					SystemDictionary.gI().getVm().getStringCache().getStringObjectref(name));
			java_class.setDebugHelperString(name + " set in createBasicTypeMirror");

			if (type != BasicType.T_VOID) {
				// ClassFile aklass = Universe.typeArrayKlassObj(type);
				// set_array_klass(java_class, aklass);
			}
			return java_class;
		}

		public static void print_signature(Objectref java_class, StringBuilder str) {
			assert (is_instance(java_class)) : "must be a Class object";

			String name = null;
			boolean is_instance = false;
			if (java_class.asClass().isPrimitive()) {
				name = VmSymbols.basicType2Signature(primitive_type(java_class));
			} else {
				is_instance = java_class.asClass().isInstance();
				name = (String) java_class.asClass().getName0();
			}
			if (name == null) {
				str.append("<null>");
				return;
			}
			if (is_instance)
				str.append("L");
			str.append(name);
			if (is_instance)
				str.append(";");
		}
	}

	public static class java_lang_boxing_object {
		private static Objectref initialize_and_allocate(BasicType type) {
			try {
				ClassFile klass = SystemDictionary.gI().getVm().classLoader
						.getClassAsClassFile(VmSymbols.basicType2JavaClassName(type));
				return klass.getAPrimitiveWrapperObjectref(SystemDictionary.gI().getVm());

			} catch (ClassFileException | PrimitiveWrappingImpossibleException e) {
				e.printStackTrace();
			}
			return null;

		}

		// oop java_lang_boxing_object::initialize_and_allocate(BasicType type, TRAPS) {
		// Klass* k = SystemDictionary::box_klass(type);
		// if (k == NULL) return NULL;
		// instanceKlassHandle h (THREAD, k);
		// if (!h->is_initialized()) h->initialize(CHECK_0);
		// return h->allocate_instance(THREAD);
		// }
		//
		//
		public static Objectref create(BasicType type, Object value) {
			Objectref ret = initialize_and_allocate(type);
			if (ret == null)
				return null;
			ret.putField(ret.getInitializedClass().getClassFile().getFieldByName("value"), value);
			return ret;
		}
		// TODO boxing.
	}

	public static class java_lang_Throwable {

	}

	public static class java_lang_invoke_MethodType {

		public static boolean is_instance(Frame frame, Objectref obj) {
			try {
				return (obj != null) && (obj.getInitializedClass().getClassFile() == frame.vm.classLoader
						.getClassAsClassFile(VmSymbols.java_lang_invoke_MethodType));
			} catch (ClassFileException e) {
				e.printStackTrace();
			}
			return false;
		}
		// Support for java_lang_invoke_MethodType

		// int java_lang_invoke_MethodType::_rtype_offset;
		// int java_lang_invoke_MethodType::_ptypes_offset;
		//
		// void java_lang_invoke_MethodType::compute_offsets() {
		// Klass* k = SystemDictionary::MethodType_klass();
		// if (k != NULL) {
		// compute_offset(_rtype_offset, k, vmSymbols::rtype_name(), vmSymbols::class_signature());
		// compute_offset(_ptypes_offset, k, vmSymbols::ptypes_name(), vmSymbols::class_array_signature());
		// }
		// }
		//
		static void print_signature(Objectref mt, StringBuilder str) {
			str.append("(");

			Arrayref pts = ptypes(mt);
			for (int i = 0; i < pts.length; i++) {
				java_lang_Class.print_signature((Objectref) pts.getElement(i), str);
			}
			str.append(")");
			java_lang_Class.print_signature(rtype(mt), str);
		}

		public static String as_signature(Objectref mt, boolean intern_if_not_found) {
			// ResourceMark rm;
			// stringStream buffer(128);
			// print_signature(mt, &buffer);
			// const char* sigstr = buffer.base();
			// int siglen = (int) buffer.size();
			// Symbol *name;
			// if (!intern_if_not_found) {
			// name = SymbolTable::probe(sigstr, siglen);
			// } else {
			// name = SymbolTable::new_symbol(sigstr, siglen, THREAD);
			// }
			StringBuilder str = new StringBuilder();

			print_signature(mt, str);
			return str.toString();
		}

		static Objectref rtype(Objectref mt) {
			// assert(is_instance(mt)):"must be a MethodType";
			return (Objectref) mt.getField(mt.getInitializedClass().getClassFile().getFieldByName("rtype"));
		}

		static Arrayref ptypes(Objectref mt) {
			// assert(is_instance(mt), "must be a MethodType");
			return (Arrayref) mt.getField(mt.getInitializedClass().getClassFile().getFieldByName("ptypes"));
		}

	}

	public static class java_lang_invoke_MemberName {
		/// MemberName accessors

		public static Objectref clazz(Objectref mname) {
			return (Objectref) mname.getField(mname.getInitializedClass().getClassFile().getFieldByName("clazz"));
		}

		public static void set_clazz(Objectref mname, Objectref clazz) {
			// assert(is_instance(mname), "wrong type");
			mname.putField(mname.getInitializedClass().getClassFile().getFieldByName("clazz"), clazz);
		}

		public static Objectref get_name(Objectref mname) {
			return (Objectref) mname.getField(mname.getInitializedClass().getClassFile().getFieldByName("name"));
		}

		public static void set_name(Objectref mname, Objectref name) {
			// assert(is_instance(mname), "wrong type");
			mname.putField(mname.getInitializedClass().getClassFile().getFieldByName("name"), name);
		}

		public static Objectref get_type(Objectref mname) {
			return (Objectref) mname.getField(mname.getInitializedClass().getClassFile().getFieldByName("type"));
		}

		public static void set_type(Objectref mname, Objectref type) {
			// assert(is_instance(mname), "wrong type");
			mname.putField(mname.getInitializedClass().getClassFile().getFieldByName("type"), type);
		}

		public static int get_flags(Objectref mname) {
			return (int) mname.getField(mname.getInitializedClass().getClassFile().getFieldByName("flags"));
		}

		public static void set_flags(Objectref mname, int flags) {
			// assert(is_instance(mname), "wrong type");
			mname.putField(mname.getInitializedClass().getClassFile().getFieldByName("flags"), flags);
		}

		public static Object get_vmtarget(Objectref mname) {
			// assert(is_instance(mname), "wrong type");
			if (mname.getSysfields().containsKey("vmtarget")) {
				return (Object) mname.getSysfields().get("vmtarget");
			} else
				return null;
		}

		/**
		 * 
		 * @param mname
		 * @param ref
		 *            Object, instanceof Method or instanceof ClasFile
		 */
		public static void set_vmtarget(Objectref mname, Object ref) {
			// assert(is_instance(mname), "wrong type");
			assert (ref instanceof ClassFile || ref instanceof Method);
			mname.getSysfields().put("vmtarget", ref);
		}

		public static Integer get_vmindex(Objectref mname) {
			// assert(is_instance(mname), "wrong type");
			if (mname.getSysfields().containsKey("vmindex")) {
				return (Integer) mname.getSysfields().get("vmindex");
			} else
				return null;

		}

		public static void set_vmindex(Objectref mname, int index) {
			// assert(is_instance(mname), "wrong type");
			mname.getSysfields().put("vmindex", index);
		}

	}

	public static void set_array_klass(Objectref java_class, ClassFile klass) {
		Globals.getInst().execLogger.warn("not impl...");
	}

}
