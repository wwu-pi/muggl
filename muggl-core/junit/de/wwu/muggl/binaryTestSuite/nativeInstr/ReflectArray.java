package de.wwu.muggl.binaryTestSuite.nativeInstr;

/**
 * Test native operations on Arrays
 * 
 * @author Max Schulze
 *
 */
public class ReflectArray {

	private static int[] testArr = new int[] { 6, 999, 5000 };

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

	public static void main(String[] args) {
		System.out.println(test_GetArrayLength());
		System.out.println(test_GetArrayElement());
		System.out.println(test_SetArrayElement());
		System.out.println(test_NewArray());
		System.out.println(test_NewMultiArray());
		System.out.println(test_IsArrayClass());
	}

}
