package de.wwu.muggl.binaryTestSuite.nativeInstr;

import java.io.Serializable;

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

	public class Testclass {

	}

	public static String test_GetClassName() {
		return ClassAndClassLoader.class.getName();
	}

	public static String test_GetClassInterfaces() {
		return ClassAndClassLoader.class.getInterfaces()[0].getName();
	}

	public static String test_GetClassLoader() {
		return ClassAndClassLoader.class.getClassLoader().toString();
	}

	public static boolean test_IsInterface() {
		// get an real interface
		return java.io.Serializable.class.isInterface();
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

	public static boolean test_IsPrimitive() {
		// only for boolean, byte, char, short, int, long, float, and double and void
		return int.class.isPrimitive();
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

	public static String test_GetClassDeclaredFields() {
		return ClassAndClassLoader.class.getDeclaredFields()[0].getName();
	}

	public static String test_GetClassDeclaredConstructors() {
		return ClassAndClassLoader.class.getDeclaredConstructors()[0].toGenericString();
	}

	public static void main(String[] args) {
		System.out.println(test_GetClassName());
		System.out.println(test_GetClassInterfaces());
		System.out.println(test_GetClassLoader());
		System.out.println(test_IsInterface());
		System.out.println(test_GetClassSigners());
		System.out.println(test_GetProtectionDomain());
		System.out.println(test_IsArrayClass());
		System.out.println("primitive: " + test_IsPrimitive());
		System.out.println(test_GetComponentType());
		System.out.println(test_GetClassModifiers());
		System.out.println(test_GetDeclaredClasses());
		System.out.println(test_GetDeclaringClass());
		System.out.println(test_GetClassAnnotations());
		System.out.println(test_GetClassDeclaredMethods());
		System.out.println(test_GetClassDeclaredFields());
		System.out.println(test_GetClassDeclaredConstructors());

	}

}
