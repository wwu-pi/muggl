package de.wwu.muggl.vm;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;

// inspired by /share/native/java/lang/vmSymbols.hpp
public class VmSymbols {
	public static final String OBJECT_INITIALIZER_NAME = "<init>";
	/**
	 * static initializer
	 */
	public static final String CLASS_INITIALIZER_NAME = "<clinit>";

	public static final String PRIORITY_NAME = "priority";
	public static final String THREADSTATUS_NAME = "threadStatus";

	public static final String SIGNATURE_BYTE = "B", SIGNATURE_CHAR = "C", SIGNATURE_DOUBLE = "D",
			SIGNATURE_FLOAT = "F", SIGNATURE_LONG = "J", SIGNATURE_SHORT = "S", SIGNATURE_BOOL = "Z",
			SIGNATURE_VOID = "V", SIGNATURE_INT = "I";

	public static final String java_lang_Byte = "java/lang/Byte", java_lang_Char = "java/lang/Character",
			java_lang_Double = "java/lang/Double", java_lang_Float = "java/lang/Float",
			java_lang_Long = "java/lang/Long", java_lang_Short = "java/lang/Short",
			java_lang_Boolean = "java/lang/Boolean", java_lang_Void = "java/lang/Void",
			java_lang_Integer = "java/lang/Integer", java_lang_Integer_IntegerCache = "java/lang/Integer$IntegerCache",
			java_lang_Object = "java/lang/Object", java_lang_String = "java/lang/String";

	public static final String java_lang_reflect_Field = "java/lang/reflect/Field";
	public static final String java_lang_Class = "java/lang/Class";
	public static final String java_lang_reflect_Method = "java/lang/reflect/Method";
	public static final String java_lang_reflect_Constructor = "java/lang/reflect/Constructor";
	public static final String java_lang_Throwable = "java/lang/Throwable";
	public static final String java_lang_invoke_MethodType = "java/lang/invoke/MethodType";

	public static final String ILLEGAL_TYPE = "<illegal type>";

	public static final String RUN_METHOD_NAME = "run";
	public static final String VOID_OBJECT_SIGNATURE = "()Ljava/lang/Object;";

	public static int RECOGNIZED_CLASS_MODIFIERS = ClassFile.ACC_PUBLIC | ClassFile.ACC_FINAL | ClassFile.ACC_SUPER
			| ClassFile.ACC_INTERFACE | ClassFile.ACC_ABSTRACT | ClassFile.ACC_ANNOTATION | ClassFile.ACC_ENUM
			| ClassFile.ACC_SYNTHETIC;
	public static int RECOGNIZED_FIELD_MODIFIERS = ClassFile.ACC_PUBLIC | ClassFile.ACC_PRIVATE
			| ClassFile.ACC_PROTECTED | ClassFile.ACC_STATIC | ClassFile.ACC_FINAL | ClassFile.ACC_VOLATILE
			| ClassFile.ACC_TRANSIENT | ClassFile.ACC_ENUM | ClassFile.ACC_SYNTHETIC;

	public static int RECOGNIZED_METHOD_MODIFIERS = ClassFile.ACC_PUBLIC | ClassFile.ACC_PRIVATE
			| ClassFile.ACC_PROTECTED | ClassFile.ACC_STATIC | ClassFile.ACC_FINAL | ClassFile.ACC_SYNCHRONIZED
			| ClassFile.ACC_BRIDGE | ClassFile.ACC_VARARGS | ClassFile.ACC_NATIVE | ClassFile.ACC_ABSTRACT
			| ClassFile.ACC_STRICT | ClassFile.ACC_SYNTHETIC;

	// flags actually written in .class file
	public static int ACC_WRITTEN_FLAGS = 0x00007FFF;

	// import java_lang_invoke_MemberName.*
	// keep these in sync. with MethodHandleNatives.Constants
	public static final int MN_IS_METHOD = 0x00010000, // method (not constructor)
			MN_IS_CONSTRUCTOR = 0x00020000, // constructor
			MN_IS_FIELD = 0x00040000, // field
			MN_IS_TYPE = 0x00080000, // nested type
			MN_CALLER_SENSITIVE = 0x00100000, // @CallerSensitive annotation detected
			MN_REFERENCE_KIND_SHIFT = 24, // refKind
			MN_REFERENCE_KIND_MASK = 0x0F000000 >> MN_REFERENCE_KIND_SHIFT,
			// The SEARCH_* bits are not for MN.flags but for the matchFlags argument of MHN.getMembers:
			MN_SEARCH_SUPERCLASSES = 0x00100000, // walk super classes
			MN_SEARCH_INTERFACES = 0x00200000; // walk implemented interfaces

	public enum BasicType {
		T_BOOLEAN(ClassFile.T_BOOLEAN), T_CHAR(ClassFile.T_CHAR), T_FLOAT(ClassFile.T_FLOAT), T_DOUBLE(
				ClassFile.T_DOUBLE), T_BYTE(ClassFile.T_BYTE), T_SHORT(ClassFile.T_SHORT), T_INT(
						ClassFile.T_INT), T_LONG(ClassFile.T_LONG), T_OBJECT(12), T_ARRAY(13), T_VOID(
								14), T_ADDRESS(15), T_NARROWOOP(16), T_METADATA(17), T_NARROWKLASS(18), T_CONFLICT(19), // for
																														// stack
																														// value
																														// type
																														// with
																														// conflicting
																														// contents
		T_ILLEGAL(99);
		public final int value;

		private BasicType(int value) {
			this.value = value;
		}

		public static BasicType[] toIdxArray() {
			int highestValue = 0;
			for (BasicType bt : BasicType.values()) {
				if (bt.value > highestValue)
					highestValue = bt.value;
			}

			BasicType[] ret = new BasicType[highestValue + 1];
			for (BasicType basicType : BasicType.values()) {
				ret[basicType.value] = basicType;
			}
			return ret;
		}

	}

	/**
	 * Return the BasicType from a Signature
	 * 
	 * @param signature
	 *            e.g. 'I'
	 * @return
	 */
	public static BasicType signature2BasicType(String signature) {
		for (int i = BasicType.T_BOOLEAN.value; i <= BasicType.T_VOID.value; i++) {
			if (TYPE_SIGNATURES[i].equals(signature)) {
				return BasicTypeArr[i];
			}
		}
		return BasicType.T_OBJECT;
	}

	/**
	 * Return a Signature for a BasicType
	 * 
	 * @param primitive_type
	 * @see BasicType
	 * @return
	 */
	public static String basicType2Signature(BasicType primitive_type) {
		return TYPE_SIGNATURES[primitive_type.value];
	}

	private static String[] TYPE_SIGNATURES;

	static {
		TYPE_SIGNATURES = new String[BasicType.T_VOID.value + 1];
		TYPE_SIGNATURES[BasicType.T_BYTE.value] = SIGNATURE_BYTE;
		TYPE_SIGNATURES[BasicType.T_CHAR.value] = SIGNATURE_CHAR;
		TYPE_SIGNATURES[BasicType.T_DOUBLE.value] = SIGNATURE_DOUBLE;
		TYPE_SIGNATURES[BasicType.T_FLOAT.value] = SIGNATURE_FLOAT;
		TYPE_SIGNATURES[BasicType.T_INT.value] = SIGNATURE_INT;
		TYPE_SIGNATURES[BasicType.T_LONG.value] = SIGNATURE_LONG;
		TYPE_SIGNATURES[BasicType.T_SHORT.value] = SIGNATURE_SHORT;
		TYPE_SIGNATURES[BasicType.T_BOOLEAN.value] = SIGNATURE_BOOL;
		TYPE_SIGNATURES[BasicType.T_VOID.value] = SIGNATURE_VOID;
		// no single signatures for T_OBJECT or T_ARRAY
		for (int i = 0; i < TYPE_SIGNATURES.length; i++) {
			if (TYPE_SIGNATURES[i] == null)
				TYPE_SIGNATURES[i] = ILLEGAL_TYPE;
		}
	}

	private static Class<?>[] TYPE_CLASSES;
	static {
		TYPE_CLASSES = new Class[BasicType.T_VOID.value + 1];
		TYPE_CLASSES[BasicType.T_BYTE.value] = byte.class;
		TYPE_CLASSES[BasicType.T_CHAR.value] = char.class;
		TYPE_CLASSES[BasicType.T_DOUBLE.value] = double.class;
		TYPE_CLASSES[BasicType.T_FLOAT.value] = float.class;
		TYPE_CLASSES[BasicType.T_INT.value] = int.class;
		TYPE_CLASSES[BasicType.T_LONG.value] = long.class;
		TYPE_CLASSES[BasicType.T_SHORT.value] = short.class;
		TYPE_CLASSES[BasicType.T_BOOLEAN.value] = boolean.class;
		TYPE_CLASSES[BasicType.T_VOID.value] = void.class;
	}

	/**
	 * Return the java Class<\?> for a BasicType
	 * 
	 * @param type
	 * @return
	 */
	public static Class<?> basicType2Class(BasicType type) {
		return TYPE_CLASSES[type.value];
	}

	private static final String[] PRIMITIVES_JAVA_CLASSNAME;
	static {
		PRIMITIVES_JAVA_CLASSNAME = new String[BasicType.T_VOID.value + 1];
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_BYTE.value] = java_lang_Byte;
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_CHAR.value] = java_lang_Char;
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_DOUBLE.value] = java_lang_Double;
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_FLOAT.value] = java_lang_Float;
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_INT.value] = java_lang_Integer;
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_LONG.value] = java_lang_Long;
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_SHORT.value] = java_lang_Short;
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_BOOLEAN.value] = java_lang_Boolean;
		PRIMITIVES_JAVA_CLASSNAME[BasicType.T_VOID.value] = java_lang_Void;

		for (int i = 0; i < PRIMITIVES_JAVA_CLASSNAME.length; i++) {
			if (PRIMITIVES_JAVA_CLASSNAME[i] == null)
				PRIMITIVES_JAVA_CLASSNAME[i] = ILLEGAL_TYPE;
		}
	}

	/**
	 * Return the ClassName for a Basic Type with'/' as delimiter
	 * 
	 * @param type
	 * @return e.g. 'java/lang/Integer'
	 */
	public static String basicType2JavaClassName(BasicType type) {
		return PRIMITIVES_JAVA_CLASSNAME[type.value];
	}

	/**
	 * Return the BasicType for a ClassName with '/' as delimiter
	 * 
	 * @param JavaClassName
	 *            e.g. 'java/lang/Integer'
	 * @return
	 */
	public static BasicType javaClassName2BasicType(String JavaClassName) {
		for (int i = BasicType.T_BOOLEAN.value; i <= BasicType.T_VOID.value; i++) {
			if (PRIMITIVES_JAVA_CLASSNAME[i].equals(JavaClassName))
				return BasicTypeArr[i];
		}
		return BasicType.T_ILLEGAL;
	}

	public static final String[] type2name_tab = { null, null, null, null, "boolean", "char", "float", "double", "byte",
			"short", "int", "long", "object", "array", "void", "*address*", "*narrowoop*", "*metadata*",
			"*narrowklass*", "*conflict*" };
	public static final String PUT_NAME = "put";
	public static final String OBJECT_OBJECT_OBJECT_SIGNATURE = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";

	/**
	 * Return the BasicType from java type name
	 * 
	 * @param name
	 *            e.g. 'boolean'
	 * @return
	 */
	public static BasicType primitiveName2BasicType(String name) {
		for (int i = BasicType.T_BOOLEAN.value; i <= BasicType.T_VOID.value; i++) {
			if (type2name_tab[i] != null && type2name_tab[i].equals(name))
				return BasicTypeArr[i];
		}
		return BasicType.T_ILLEGAL;
	}

	/**
	 * Holds the BasicTypes, indexed by their .value
	 */
	public static BasicType[] BasicTypeArr = BasicType.toIdxArray();

	public static Object wideningPrimConversion(Object val, Class<?> target) {
		if (val.getClass().getName().equals(target.getName())) {
			return val;
		}
		// see also
		// http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/sun/invoke/util/Wrapper.java#540
		if (target == Integer.class) {
			switch (val.getClass().getName()) {
			case "java.lang.Character":
				// getting here because of java.lang.invoke.MethodType.insertParameterTypes Line 35: Executing iadd
				// maybe debug that better...
				return (int) (Character) val;
			case "java.lang.Byte":
				return Byte.valueOf((byte) val).intValue();
			case "java.lang.Boolean":
				return ((boolean) val) ? 1 : 0;
			case "java.lang.Short":
				return Short.valueOf((short) val).intValue();
			}
		}

		Globals.getInst().execLogger.debug(
				"wideningPrimConversion: " + val.getClass().getName() + " to " + target.getName() + " not implemented");
		return val;
	}

	public static void initialize() {

	}

}
