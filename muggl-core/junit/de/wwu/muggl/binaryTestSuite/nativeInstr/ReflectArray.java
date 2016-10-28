package de.wwu.muggl.binaryTestSuite.nativeInstr;

/**
 * Test native operations on Arrays
 * 
 * @author Max Schulze
 *
 */
public class ReflectArray {

	private static int[] testArr = new int[] { 6, 999, 5000 };

	// currently misses a lot of those

	// Public Method Static Native get(Object, int) : Object
	// Public Method Static Native getBoolean(Object, int) : boolean
	// Public Method Static Native getByte(Object, int) : byte
	// Public Method Static Native getChar(Object, int) : char
	// Public Method Static Native getDouble(Object, int) : double
	// Public Method Static Native getFloat(Object, int) : float
	// Public Method Static Native getInt(Object, int) : int
	// Public Method Static Native getLength(Object) : int
	// Public Method Static Native getLong(Object, int) : long
	// Public Method Static Native getShort(Object, int) : short

	// Public Method Static Native Set(Object, int, Object) : void
	// Public Method Static Native SetBoolean(Object, int, boolean) : void
	// Public Method Static Native SetByte(Object, int, byte) : void
	// Public Method Static Native SetChar(Object, int, char) : void
	// Public Method Static Native SetDouble(Object, int, double) : void
	// Public Method Static Native SetFloat(Object, int, float) : void
	// Public Method Static Native SetInt(Object, int, int) : void
	// Public Method Static Native SetLong(Object, int, long) : void
	// Public Method Static Native SetShort(Object, int, short) : void

	public static int test_GetArrayLength() {
		return java.lang.reflect.Array.getLength(testArr);
	}

	public static int test_GetArrayElement() {
		return java.lang.reflect.Array.getInt(testArr, 1);
	}

	public static int test_SetArrayElement() {
		java.lang.reflect.Array.setInt(testArr, 1, 1000);
		return testArr[1];
	}

	public static int test_NewArray() {
		Object mTest = java.lang.reflect.Array.newInstance(int.class, 10);
		return ((int[]) mTest)[1];
	}

	public static int test_NewMultiArray() {
		Object mTest = java.lang.reflect.Array.newInstance(int.class, new int[] { 2, 4 });
		return ((int[][]) mTest)[0].length;
	}

	public static boolean test_IsArrayClass() {
		Object mTest = java.lang.reflect.Array.newInstance(int.class, 10);
		return mTest.getClass().isArray();
	}

	public static String test_ArrayGetClass() {
		// ex [I , [Ljava/lang/Integer; , [Lde.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray;
		Object mTest = java.lang.reflect.Array.newInstance(int.class, 10);
		return mTest.getClass().getName();
	}

	public static String test_ArrayGetComponentTypeClass() {
		Object mTest = java.lang.reflect.Array.newInstance(int.class, 10);
		return mTest.getClass().getComponentType().getName();
	}

	public static void main(String[] args) {
		System.out.println(test_GetArrayLength());
		System.out.println(test_GetArrayElement());
		System.out.println(test_SetArrayElement());
		System.out.println(test_NewArray());
		System.out.println(test_NewMultiArray());
		System.out.println(test_IsArrayClass());
		System.out.println(test_ArrayGetClass());
		System.out.println(test_ArrayGetComponentTypeClass());
	}

}
