package de.wwu.muggl.vm;

// inspired by /share/native/java/lang/vmSymbols.hpp
public class VmSymbols {
	public static final String OBJECT_INITIALIZER_NAME = "<init>";
	public static final String CLASS_INITIALIZER_NAME = "<clinit>";

	public static final String PRIORITY_NAME = "priority";
	public static final String THREADSTATUS_NAME = "threadStatus";

	public static final String SIGNATURE_BYTE = "B";
	public static final String SIGNATURE_CHAR = "C";
	public static final String SIGNATURE_DOUBLE = "D";
	public static final String SIGNATURE_FLOAT = "F";
	public static final String SIGNATURE_LONG = "J";
	public static final String SIGNATURE_SHORT = "S";
	public static final String SIGNATURE_BOOL = "Z";
	public static final String SIGNATURE_VOID = "V";
	public static final String SIGNATURE_INT = "I";

	public static final String PrimitiveStr2ClassStr(final String primitive) {
		switch (primitive) {
		case SIGNATURE_BYTE:
			return "java.lang.Byte";
		case SIGNATURE_CHAR:
			return "java.lang.Character";
		case SIGNATURE_DOUBLE:
			return "java.lang.Double";
		case SIGNATURE_FLOAT:
			return "java.lang.Float";
		case SIGNATURE_LONG:
			return "java.lang.Long";
		case SIGNATURE_SHORT:
			return "java.lang.Short";
		case SIGNATURE_BOOL:
			return "java.lang.Boolean";
		case SIGNATURE_INT:
			return "java.lang.Integer";
		default:
			return null;
		}
	}

	public static final String ClassStr2PrimitiveStr(final String classStr) {
		switch (classStr) {
		case "java.lang.Byte":
			return SIGNATURE_BYTE;
		case "java.lang.Character":
			return SIGNATURE_CHAR;
		case "java.lang.Double":
			return SIGNATURE_DOUBLE;
		case "java.lang.Float":
			return SIGNATURE_FLOAT;
		case "java.lang.Long":
			return SIGNATURE_LONG;
		case "java.lang.Short":
			return SIGNATURE_SHORT;
		case "java.lang.Boolean":
			return SIGNATURE_BOOL;
		case "java.lang.Integer":
			return SIGNATURE_INT;
		default:
			return null;
		}
	}

	public static final Class<?> ClassStr2PrimitiveClazz(final String classStr) {
		switch (classStr) {
		case "java.lang.Byte":
			return byte.class;
		case "java.lang.Character":
			return char.class;
		case "java.lang.Double":
			return double.class;
		case "java.lang.Float":
			return float.class;
		case "java.lang.Long":
			return long.class;
		case "java.lang.Short":
			return short.class;
		case "java.lang.Boolean":
			return boolean.class;
		case "java.lang.Integer":
			return Integer.class;
		default:
			return null;
		}
	}
}
