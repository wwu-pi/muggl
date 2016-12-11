package de.wwu.muggl.binaryTestSuite.nativeInstr;

import java.io.Serializable;
import de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.MySecondType;

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

	public static final String METHOD_test_GetClassForName = "test_GetClassForName";

	public static String test_GetClassForName() {
		try {
			return Class.forName("java.lang.Object").getName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static final String METHOD_test_MethodWithParamsAndException = "test_MethodWithParamsAndException";

	public static String test_MethodWithParamsAndException(int testing) throws ClassNotFoundException {
		return "";
	}

	public static final String METHOD_test_GetClassForNameWithException = "test_GetClassForNameWithException";

	public static String test_GetClassForNameWithException() {
		try {
			return Class.forName("java.lang.Object1").getName();
		} catch (ClassNotFoundException e) {
			return "success";
		}
	}

	public static final String METHOD_test_GetClassName = "test_GetClassName";

	public static String test_GetClassName() {
		return ClassAndClassLoader.class.getName();
	}

	public static final String METHOD_test_GetClassNameForPrimitive = "test_GetClassNameForPrimitive";

	public static String test_GetClassNameForPrimitive() {
		return int.class.getName();
	}

	public static final String METHOD_test_GetClassNameObj = "test_GetClassNameObj";

	public static String test_GetClassNameObj() {
		return ((Object) 5).getClass().getName();
	}

	public static final String METHOD_test_GetClassInterfaces = "test_GetClassInterfaces";

	public static String test_GetClassInterfaces() {
		return ClassAndClassLoader.class.getInterfaces()[0].getName();
	}

	public static final String METHOD_test_GetClassLoader = "test_GetClassLoader";

	public static String test_GetClassLoader() {
		return ClassAndClassLoader.class.getClassLoader().toString();
	}

	public static final String METHOD_test_IsInterface = "test_IsInterface";

	public static int test_IsInterface() {
		int interfaces = 0;
		// get an real interface
		if (java.io.Serializable.class.isInterface())
			interfaces++;

		if (ClassAndClassLoader.class.isInterface())
			interfaces++;

		return interfaces;
	}

	public static final String METHOD_test_isAssignableFrom = "test_isAssignableFrom";

	public static int test_isAssignableFrom() {
		int tests = 0;

		// same class
		if (int.class.isAssignableFrom(int.class))
			tests++;

		// superclass = widening reference
		if (Object.class.isAssignableFrom(Integer.class))
			tests++;
		
		return tests;
	}

	public static final String METHOD_test_GetClassSigners = "test_GetClassSigners";

	public static int test_GetClassSigners() {
		Object[] signers = ClassAndClassLoader.class.getSigners();

		// this is really not a good test...
		if (signers != null) {
			return signers.length;
		} else
			return -1;
	}

	// no test for setClassSigners
	public static final String METHOD_test_GetProtectionDomain = "test_GetProtectionDomain";

	public static String test_GetProtectionDomain() {
		return ClassAndClassLoader.class.getProtectionDomain().toString();
	}

	public static final String METHOD_test_IsArrayClass = "test_IsArrayClass";

	public static boolean test_IsArrayClass() {
		return ClassAndClassLoader.class.isArray();
	}

	public static final String METHOD_test_CountPrimitive = "test_CountPrimitive";

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

	public static final String METHOD_test_CountPrimitiveInstances = "test_CountPrimitiveInstances";

	public static int test_CountPrimitiveInstances() {
		int countPrims = 0;
		int countNonPrims = 0;
		{
			// need to use Component Type because case to Object would destroy .isPrimitive information
			boolean[] a = { true };
			if (a.getClass().getComponentType().isPrimitive())
				countPrims++;
			byte b[] = { 2 };
			if (b.getClass().getComponentType().isPrimitive())
				countPrims++;
			char[] c = { 'c' };
			if (c.getClass().getComponentType().isPrimitive())
				countPrims++;
			short[] d = { 3 };
			if (d.getClass().getComponentType().isPrimitive())
				countPrims++;
			int[] e = { 4 };
			if (e.getClass().getComponentType().isPrimitive())
				countPrims++;
			long[] f = { 5 };
			if (f.getClass().getComponentType().isPrimitive())
				countPrims++;
			float[] g = { 6 };
			if (g.getClass().getComponentType().isPrimitive())
				countPrims++;
			double[] h = { 7.24 };
			if (h.getClass().getComponentType().isPrimitive())
				countPrims++;
		}

		// can use object directly here
		Boolean a = true;
		if (!a.getClass().isPrimitive())
			countNonPrims++;
		Byte[] b = { 2 };
		if (!b.getClass().getComponentType().isPrimitive())
			countNonPrims++;
		Character c = 'c';
		if (!c.getClass().isPrimitive())
			countNonPrims++;
		Short d = 3;
		if (!d.getClass().isPrimitive())
			countNonPrims++;
		Integer e = 4;
		if (!e.getClass().isPrimitive())
			countNonPrims++;
		Long f = (long) 5;
		if (!f.getClass().isPrimitive())
			countNonPrims++;
		Float g = (float) 6;
		if (!g.getClass().isPrimitive())
			countNonPrims++;
		Double h = 7.24;
		if (!h.getClass().isPrimitive())
			countNonPrims++;

		return countPrims * countNonPrims;
	}

	public static final String METHOD_test_isInstance = "test_isInstance";

	public static boolean test_isInstance() {
		Integer test = 3;
		return Object.class.isInstance(test);
	}

	public static final String METHOD_test_GetComponentType = "test_GetComponentType";

	public static String test_GetComponentType() {
		return (new long[0]).getClass().getComponentType().getName();
	}

	public static final String METHOD_test_GetComponentType2 = "test_GetComponentType2";

	public static int test_GetComponentType2() {
		int ret = 0;
		if (Integer[].class.getComponentType().getName() == "java.lang.Integer")
			ret++;
		if (Integer[][].class.getComponentType().getName() == "[Ljava.lang.Integer;")
			ret++;

		Class<?> klazz = int[][].class.getComponentType();
		if (klazz.getName() == "[I")
			ret++;

		return ret;
	}

	public static final String METHOD_test_GetComponentTypeIdentity = "test_GetComponentTypeIdentity";

	public static boolean test_GetComponentTypeIdentity() {
		return Integer[][].class.getComponentType() == Integer[][].class.getComponentType();
	}

	public static final String METHOD_test_GetClassModifiers = "test_GetClassModifiers";

	public static int test_GetClassModifiers() {
		return ClassAndClassLoader.class.getModifiers();
	}

	public static final String METHOD_test_GetDeclaredClasses = "test_GetDeclaredClasses";

	public static String test_GetDeclaredClasses() {
		return ClassAndClassLoader.class.getDeclaredClasses()[0].getName();
	}

	public static final String METHOD_test_GetDeclaringClass = "test_GetDeclaringClass";

	public static String test_GetDeclaringClass() {
		return Testclass.class.getDeclaringClass().getName();
	}

	public static final String METHOD_test_GetDeclaringClassOnPrimitive = "test_GetDeclaringClassOnPrimitive";

	public static String test_GetDeclaringClassOnPrimitive() {
		Class<?> ret = int.class.getDeclaringClass();
		if (ret != null)
			return ret.getName();
		else
			return "null";
	}

	public static final String METHOD_test_GetEnclosingClass = "test_GetEnclosingClass";

	public static String test_GetEnclosingClass() {
		return MySecondType.class.getEnclosingClass().getName();
	}

	// no test for JVM_GetClasSignature
	public static final String METHOD_test_GetClassAnnotations = "test_GetClassAnnotations";

	public static String test_GetClassAnnotations() {
		return ClassAndClassLoader.class.getAnnotations()[0].toString();
	}

	// no test for JVM_GetClassTypeAnnotations

	// no test for JVM_GetFieldTypeAnnotations

	// no test for JVM_GetMethodTypAnnotations
	public static final String METHOD_test_GetClassDeclaredMethods = "test_GetClassDeclaredMethods";

	public static String test_GetClassDeclaredMethods() {
		return ClassAndClassLoader.class.getDeclaredMethods()[0].getName();
	}

	public static final String METHOD_test_GetClassArrayComponentType = "test_GetClassArrayComponentType";

	public static String test_GetClassArrayComponentType() {
		Class<?>[] arr = new Class[0];
		return arr.getClass().getComponentType().getName();
	}

	public static final String METHOD_test_GetClassDeclaredFieldsCount = "test_GetClassDeclaredFieldsCount";

	public static int test_GetClassDeclaredFieldsCount() {
		return ClassAndClassLoader.class.getDeclaredFields().length;
	}

	public static final String METHOD_test_GetClassDeclaredFields = "test_GetClassDeclaredFields";

	public static String test_GetClassDeclaredFields() {
		return ClassAndClassLoader.class.getDeclaredFields()[1].getName();
	}

	public static final String METHOD_test_GetClassDeclaredConstructors = "test_GetClassDeclaredConstructors";

	public static String test_GetClassDeclaredConstructors() {
		return ClassAndClassLoader.class.getDeclaredConstructors()[0].toGenericString();
	}

	public static final String METHOD_test_ObjectClass = "test_ObjectClass";

	@SuppressWarnings("unused")
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

	public static final String METHOD_test_GetGetClassPrimitive = "test_GetGetClassPrimitive";

	public static String test_GetGetClassPrimitive() {
		String ret = char.class.getTypeName();
		// System.out.println(ret);
		return ret;
	}

	public static final String METHOD_test_GetClassPrimitiveNames = "test_GetClassPrimitiveNames";

	public static String test_GetClassPrimitiveNames() {

		return boolean.class.getSimpleName() + byte.class.getSimpleName() + short.class.getSimpleName()
				+ char.class.getSimpleName() + int.class.getSimpleName() + long.class.getSimpleName()
				+ float.class.getSimpleName() + double.class.getSimpleName() + Object.class.getSimpleName()
				+ void.class.getSimpleName();
	}

	public static final String METHOD_test_PrimitiveClassesReferenceEqual = "test_PrimitiveClassesReferenceEqual";

	public static boolean test_PrimitiveClassesReferenceEqual() {
		long[] a = { 2 };

		boolean ra = long.class == a.getClass().getComponentType();
		boolean rb = long.class == Long.TYPE;
		return ra && rb;
	}

	public static final String METHOD_test_GetClassAccessFlags = "test_GetClassAccessFlags";

	@SuppressWarnings("restriction")
	public static int test_GetClassAccessFlags() {
		Class<?> clazz = Class.class;
		return sun.reflect.Reflection.getClassAccessFlags(clazz);
	}

	// no test for JVM_GetClassConstantPool

	public static void main(String[] args) {
		System.out.println(test_GetClassName());
		System.out.println(test_GetClassInterfaces());
		System.out.println(test_GetClassLoader());
		System.out.println(test_IsInterface());
		System.out.println(test_GetClassSigners());
		System.out.println(test_GetProtectionDomain());
		System.out.println(test_IsArrayClass());
		System.out.println("primitive (via .class): " + test_CountPrimitive());
		System.out.println("primitive (via Object .getClass): " + test_CountPrimitiveInstances());
		System.out.println(test_GetComponentType());
		System.out.println(test_GetClassModifiers());
		System.out.println("declaredClasses: " + test_GetDeclaredClasses());
		System.out.println(test_GetDeclaringClass());
		System.out.println(test_GetClassAnnotations());
		System.out.println(test_GetClassDeclaredMethods());
		System.out.println(test_GetClassDeclaredFields());
		System.out.println(test_GetClassDeclaredConstructors());
		System.out.println(test_GetClassDeclaredFieldsCount());
		System.out.println(test_GetGetClassPrimitive());
		System.out.println(test_ObjectClass());
		System.out.println(test_GetDeclaringClassOnPrimitive());
		System.out.println(test_PrimitiveClassesReferenceEqual());
		System.out.println(test_GetClassArrayComponentType());
		System.out.println(test_GetClassAccessFlags());
		System.out.println(test_GetComponentType2());
		System.out.println(test_GetComponentTypeIdentity());
		System.out.println(test_isInstance());
		System.out.println(test_isAssignableFrom());
	}

}
