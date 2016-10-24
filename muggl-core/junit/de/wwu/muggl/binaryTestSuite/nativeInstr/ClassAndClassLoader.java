package de.wwu.muggl.binaryTestSuite.nativeInstr;

import java.io.Serializable;

import sun.reflect.Reflection;

/**
 * Testing native instructions in java.lang.Class and .ClassLoader
 * 
 * @author Max Schulze
 *
 */
@MyAnnotation
public class ClassAndClassLoader implements Serializable {

	// untestable - throws exception java.lang.InternalError: CallerSensitive annotation expected at frame 1
	// @CallerSensitive
	// public static String test_GetCallerClass() {
	// Class<?> clazz = sun.reflect.Reflection.getCallerClass();
	// return clazz.getName();
	// }

	// no tests for the following (uses openjdk/jdk/src/share/javavm/export/jvm.h function names):
	// JVM_FindPrimitiveClass / getPrimitiveClass
	// JVM_ResolveClass
	// JVM_FindClassFromBootLoader
	// JVM_FindClassFromClassLoader
	// JVM_FindClassFromClass
	// JVM_FindLoadedClass
	// JVM_DefineClass
	// JVM_DefineClassWithSource

	private static final long serialVersionUID = -943481304595132274L;
	@SuppressWarnings("unused")
	private static final long mySuperSpecialField = 42;

	public class Testclass {

	}

	public static String test_GetClassForName() {
		try {
			return Class.forName("java.lang.Object").getName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String test_MethodWithParamsAndException(int testing) throws ClassNotFoundException {
		return "";
	}

	public static String test_GetClassForNameWithException() {
		try {
			return Class.forName("java.lang.Object1").getName();
		} catch (ClassNotFoundException e) {
			return "success";
		}
	}

	public static String test_GetClassName() {
		return ClassAndClassLoader.class.getName();
	}

	public static String test_GetClassNameForPrimitive() {
		return int.class.getName();
	}

	public static String test_GetClassNameObj() {
		return ((Object) 5).getClass().getName();
	}

	public static String test_GetClassInterfaces() {
		return ClassAndClassLoader.class.getInterfaces()[0].getName();
	}

	public static String test_GetClassLoader() {
		return ClassAndClassLoader.class.getClassLoader().toString();
	}

	public static int test_IsInterface() {
		int interfaces = 0;
		// get an real interface
		if (java.io.Serializable.class.isInterface())
			interfaces++;

		if (ClassAndClassLoader.class.isInterface())
			interfaces++;

		return interfaces;
	}

	public static int test_GetClassSigners() {
		Object[] signers = ClassAndClassLoader.class.getSigners();

		// this is really not a good test...
		if (signers != null) {
			return signers.length;
		} else
			return -1;
	}

	// no test for setClassSigners

	public static String test_GetProtectionDomain() {
		return ClassAndClassLoader.class.getProtectionDomain().toString();
	}

	public static boolean test_IsArrayClass() {
		return ClassAndClassLoader.class.isArray();
	}

	public static int test_CountPrimitive() {
		int countPrims = 0;
		int countNonPrims = 0;
		// only for boolean, byte, char, short, int, long, float, and double and void
		if (boolean.class.isPrimitive())
			countPrims++;
		if (byte.class.isPrimitive())
			countPrims++;
		if (char.class.isPrimitive())
			countPrims++;
		if (short.class.isPrimitive())
			countPrims++;
		if (int.class.isPrimitive())
			countPrims++;
		if (long.class.isPrimitive())
			countPrims++;
		if (float.class.isPrimitive())
			countPrims++;
		if (double.class.isPrimitive())
			countPrims++;
		if (void.class.isPrimitive())
			countPrims++;

		if (!Boolean.class.isPrimitive())
			countNonPrims++;
		if (!Byte.class.isPrimitive())
			countNonPrims++;
		if (!Character.class.isPrimitive())
			countNonPrims++;
		if (!Short.class.isPrimitive())
			countNonPrims++;
		if (!Integer.class.isPrimitive())
			countNonPrims++;
		if (!Long.class.isPrimitive())
			countNonPrims++;
		if (!Float.class.isPrimitive())
			countNonPrims++;
		if (!Double.class.isPrimitive())
			countNonPrims++;
		if (!Void.class.isPrimitive())
			countNonPrims++;
		if (!Object.class.isPrimitive())
			countNonPrims++;

		return countPrims * countNonPrims;
	}

	public static String test_GetComponentType() {
		return (new long[0]).getClass().getComponentType().getName();
	}

	public static int test_GetClassModifiers() {
		return ClassAndClassLoader.class.getModifiers();
	}

	public static String test_GetDeclaredClasses() {
		return ClassAndClassLoader.class.getDeclaredClasses()[0].getName();
	}

	public static String test_GetDeclaringClass() {
		return Testclass.class.getDeclaringClass().getName();
	}

	// no test for JVM_GetClasSignature

	public static String test_GetClassAnnotations() {
		return ClassAndClassLoader.class.getAnnotations()[0].toString();
	}

	// no test for JVM_GetClassTypeAnnotations

	// no test for JVM_GetFieldTypeAnnotations

	// no test for JVM_GetMethodTypAnnotations

	public static String test_GetClassDeclaredMethods() {
		return ClassAndClassLoader.class.getDeclaredMethods()[0].getName();
	}

	public static int test_GetClassDeclaredFieldsCount() {
		return ClassAndClassLoader.class.getDeclaredFields().length;
	}

	public static String test_GetClassDeclaredFields() {
		return ClassAndClassLoader.class.getDeclaredFields()[1].getName();
	}

	public static String test_GetClassDeclaredConstructors() {
		return ClassAndClassLoader.class.getDeclaredConstructors()[0].toGenericString();
	}

	public static int test_ObjectClass() {
		// multi-stage testing that we get the object class object right
		Class<?> klazz = Object.class;

		if (klazz == null)
			return 0;

		if (!klazz.getName().equals("java.lang.Object"))
			return 1;

		if (!klazz.getSimpleName().equals("Object"))
			return 2;

		return -1;
	}

	public static String test_GetGetClassPrimitive() {
		String ret = char.class.getTypeName();
		// System.out.println(ret);
		return ret;
	}

	public static String test_GetClassPrimitiveNames() {

		return boolean.class.getSimpleName() + byte.class.getSimpleName() + short.class.getSimpleName()
				+ char.class.getSimpleName() + int.class.getSimpleName() + long.class.getSimpleName()
				+ float.class.getSimpleName() + double.class.getSimpleName() + Object.class.getSimpleName()
				+ void.class.getSimpleName();
	}

	// no test for JVM_GetClassAccessFlags
	// no test for JVM_GetClassConstantPool

	public static void main(String[] args) {
		System.out.println(test_GetClassName());
		System.out.println(test_GetClassInterfaces());
		System.out.println(test_GetClassLoader());
		System.out.println(test_IsInterface());
		System.out.println(test_GetClassSigners());
		System.out.println(test_GetProtectionDomain());
		System.out.println(test_IsArrayClass());
		System.out.println("primitive: " + test_CountPrimitive());
		System.out.println(test_GetComponentType());
		System.out.println(test_GetClassModifiers());
		System.out.println(test_GetDeclaredClasses());
		System.out.println(test_GetDeclaringClass());
		System.out.println(test_GetClassAnnotations());
		System.out.println(test_GetClassDeclaredMethods());
		System.out.println(test_GetClassDeclaredFields());
		System.out.println(test_GetClassDeclaredConstructors());
		System.out.println(test_GetClassDeclaredFieldsCount());
		System.out.println(test_GetGetClassPrimitive());
		System.out.println(test_ObjectClass());
	}

}
