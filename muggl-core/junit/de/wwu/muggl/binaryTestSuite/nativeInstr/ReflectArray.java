package de.wwu.muggl.binaryTestSuite.nativeInstr;

import java.util.Arrays;

import de.wwu.muggl.test.TestSkeleton;

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

	public static int test_GetArrayElementMultiple() {
		int ret = 0;

		Object[] arr0 = { ReflectArray.class };
		if (java.lang.reflect.Array.get(arr0, 0) == ReflectArray.class)
			ret++;

		boolean[] arr1 = { true, false };
		if (java.lang.reflect.Array.getBoolean(arr1, 0) == true)
			ret++;

		byte[] arr2 = { 0x01, 0x07 };
		if (java.lang.reflect.Array.getByte(arr2, 1) == 0x07)
			ret++;

		char[] arr3 = { 'c', 'd' };
		if (java.lang.reflect.Array.getChar(arr3, 1) == 'd')
			ret++;

		double[] arr4 = { 1.11d, 1.23d };
		if (java.lang.reflect.Array.getDouble(arr4, 1) == 1.23)
			ret++;

		float[] arr5 = { 1.11f, 1.23f };
		if (java.lang.reflect.Array.getFloat(arr5, 1) == 1.23f)
			ret++;

		int[] arr6 = { 35512, 35511 };
		if (Integer.compare(java.lang.reflect.Array.getInt(arr6, 1), 35511) == 0)
			ret++;

		long[] arr7 = { 35512, 9223372036854775800L };
		if (Long.compare(java.lang.reflect.Array.getLong(arr7, 1), 9223372036854775800L) == 0)
			ret++;

		short[] arr8 = { 077, 076 };
		if (Short.compare(java.lang.reflect.Array.getShort(arr8, 1), (short) 076) == 0)
			ret++;

		return ret;
	}

	public static int test_SetArrayElementMultipleIntegerBased() {
		int ret = 0;

		Object[] arr0 = { Integer.class };
		java.lang.reflect.Array.set(arr0, 0, boolean.class);
		if (java.lang.reflect.Array.get(arr0, 0) == boolean.class)
			ret++;

		boolean[] arr1 = { false, true };
		java.lang.reflect.Array.setBoolean(arr1, 0, true);
		if (java.lang.reflect.Array.getBoolean(arr1, 0) == true)
			ret++;

		byte[] arr2 = { 0x01, 0x05 };
		java.lang.reflect.Array.setByte(arr2, 1, (byte) 0x07);
		if (java.lang.reflect.Array.getByte(arr2, 1) == 0x07)
			ret++;

		double[] arr4 = { 1.11d, 1.23d };
		java.lang.reflect.Array.setDouble(arr4, 1, -0.99d);
		if (java.lang.reflect.Array.getDouble(arr4, 1) == -0.99d)
			ret++;

		float[] arr5 = { 1.11f, 1.23f };
		java.lang.reflect.Array.setFloat(arr5, 1, -3.45f);
		if (java.lang.reflect.Array.getFloat(arr5, 1) == -3.45f)
			ret++;

		int[] arr6 = { 35512, 35511 };
		java.lang.reflect.Array.setInt(arr6, 1, -34111);
		if (Integer.compare(java.lang.reflect.Array.getInt(arr6, 1), -34111) == 0)
			ret++;
		long[] arr7 = { 35512, 9223372036854775800L };
		java.lang.reflect.Array.setLong(arr7, 1, 9223372036814775800L);
		if (Long.compare(java.lang.reflect.Array.getLong(arr7, 1), 9223372036814775800L) == 0)
			ret++;

		return ret;
	}

	public static int test_SetArrayElementShort() {
		int ret = 0;
		short[] arr8 = { 077, 076 };
		java.lang.reflect.Array.setShort(arr8, 1, (short) 072);
		if (Short.compare(java.lang.reflect.Array.getShort(arr8, 1), (short) 072) == 0)
			ret++;

		return ret;
	}

	public static int test_SetArrayElementChar() {
		int ret = 0;
		char[] arr3 = { 'c', 'd' };
		java.lang.reflect.Array.setChar(arr3, 1, 'a');
		if (java.lang.reflect.Array.getChar(arr3, 1) == 'a')
			ret++;

		return ret;
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
		Object mTest = java.lang.reflect.Array.newInstance(int.class, new int[] { 2, 4, 3 });
		return ((int[][][]) mTest)[0][1].length;
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

	public static String METHOD_test_ArrayCopyOf = "test_ArrayCopyOf";

	public static boolean test_ArrayCopyOf() {
		Integer[] orig = { new Integer(10), new Integer(11) };
		Integer[] copy = Arrays.copyOf(orig, 10);

		return (copy[0] == orig[0]);
	}

	public static String METHOD_test_ArrayCopyOfRange = "test_ArrayCopyOfRange";

	public static boolean test_ArrayCopyOfRange() {
		Integer[] orig = { new Integer(10), new Integer(12) };
		Integer[] copy = Arrays.copyOfRange(orig, 1, 2, Integer[].class);

		return (copy[0] == orig[1]);
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
		System.out.println(test_ArrayCopyOf());
		System.out.println(test_ArrayCopyOfRange());
	}

}
