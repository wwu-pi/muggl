package de.wwu.muggl.binaryTestSuite;

import java.util.ArrayList;
import java.util.List;

public class ArrayrefToObjectref {

	public final static String METHOD_testArrayLength = "testArrayLength";

	// test does only produce raw array instructions
	public static int testArrayLength() {

		int[] meinArray = { 1, 2, 3, 4, 5 };
		return meinArray.length;
	}

	public final static String METHOD_testArrayToString = "testArrayToString";

	// test produces bytecode: invokevirtual #20 // Method java/lang/Object.toString:()Ljava/lang/String;
	public static String testArrayToString() {
		int[] meinArray = { 1, 2, 3, 4, 5 };
		return meinArray.toString();
	}

	public final static String METHOD_arrayGetSuperclass = "test_arrayGetSuperclass";

	public static String test_arrayGetSuperclass() {
		int[][] meinArray = { { 1, 2 }, { 3 }, { 4 } };
		Object test = meinArray;

		// java.lang.Object.getClass needed in java.util.Arrays.copyOf
		Class<?> clazz = test.getClass();
		return clazz.getSuperclass().getName();
	}

	public final static String METHOD_arrayClone = "test_arrayClone";

	public static String test_arrayClone() {
		int[] a = { 1, 2, 3 };
		int[] b = a.clone();
		// System.out.println("cloning done..");
		return b.length + "" + b.toString();
	}

	public final static String METHOD_testMultiDimArrayGetClass = "testMultiDimArrayGetClass";

	public static String testMultiDimArrayGetClass() {
		int[][] meinArray = { { 1, 2 }, { 3 }, { 4 } };
		Object test = meinArray;
		// java.lang.Object.getClass needed in java.util.Arrays.copyOf

		return test.getClass().getName();
	}

	public final static String METHOD_testStringrefClassName = "testStringrefClassName";

	public static String testStringrefClassName() {
		int[] meinArray = { 1 };
		Object test = meinArray;
		// java.lang.Object.getClass needed in java.util.Arrays.copyOf
		return test.getClass().getName() + " blablabla";
	}

	public final static String METHOD_testMultipleInstanceOf = "testMultipleInstanceOf";

	public static String testMultipleInstanceOf() {
		int[][] meinArray = { { 1, 2 }, {}, { 4 } };
		// FIXME mxs: rework when IntegerCache ready
		// Integer[][] meinIntegerArray = { { 1, 2 }, { 3 }, { 4 } };
		Object[] testing = { meinArray };
		return meinArray.getClass().getName() + testing.getClass().getName() + testing[0].getClass().getName();
	}

	public final static String METHOD_testArrayClassEquivalence = "testArrayClassEquivalence";

	// taken from the JLS 10.8.2
	public static boolean testArrayClassEquivalence() {
		int[] ia = new int[3];
		int[] ib = new int[6];
		// System.out.println(ia == ib); // false
		return ia.getClass() == ib.getClass(); // true
	}

	public final static String METHOD_testArraySubarrayClone = "testArraySubarrayClone";

	// JLS 10.7.2
	public static boolean testArraySubarrayClone() throws Throwable {
		int ia[][] = { { 65142, 65142 }, null }; // use Integers beyond IntegerCache to really make sure references are
													// kept equal
		int ja[][] = ia.clone();
		// System.out.print((ia == ja) + " ");// false
		return ia[0] == ja[0] && ia[1] == ja[1];
	}

	public final static String METHOD_testBooleanTreatedAsInt = "testBooleanTreatedAsInt";

	// produces a baload and ireturn instruction
	public static boolean testBooleanTreatedAsInt() {
		boolean[] mBoolean = { true, false };
		return mBoolean[0];
	}

	public final static String METHOD_testAllPrimitiveArrayTypes = "testAllPrimitiveArrayTypes";

	public static String testAllPrimitiveArrayTypes() {
		byte[] mByte = { 0x1, 0x2 };
		char[] mChar = { 'a', 'B' };
		double[] mDouble = { 1.27, 2.22 };
		float[] mFloat = { 2, 3 };
		long[] mLong = { 4, 7 };
		short[] mShort = { 9, 10 };
		boolean[] mBoolean = { true, false };
		int[] mInt = { 1, 2 };

		Object[] all = { mByte, mChar, mDouble, mFloat, mLong, mShort, mBoolean, mInt };

		String types = "";
		for (int i = 0; i < all.length; i++) {
			types = types + all[i].getClass().getName();
		}
		return types;
	}
	
	public final static String METHOD_testArrayComponentTypeClass = "testArrayComponentTypeClass";

	public static boolean testArrayComponentTypeClass() {
		byte[] mByte = { 0x1, 0x2 };
				
		return mByte.getClass().getComponentType() == byte.class;
	}

	public final static String METHOD_testArrayInstanceOf = "testArrayInstanceOf";

	public static boolean testArrayInstanceOf() {
		int[] meinArray = { 1, 2, 3, 4, 5 };
		Object test = meinArray;
		return (test instanceof Object[] && meinArray instanceof int[]);
	}

	public final static String METHOD_testIntegerPutByteArray = "testIntegerPutByteArray";

	public static int testIntegerPutByteArray() {
		byte[] array = new byte[5];

		array[3] = 2 + 4;
		return array[3];
	}

	public final static String METHOD_testIntegerPutByteArray2 = "testIntegerPutByteArray2";

	// half a boxing issue, half array - put
	public static int testIntegerPutByteArray2() {
		Byte[] array = new Byte[5];
		array[3] = 2 + 4;
		return array[3];
	}

	public final static String METHOD_ArrayRefObjectrefBoolean = "ArrayRefObjectrefBoolean";

	public static boolean ArrayRefObjectrefBoolean() {
		List<Boolean> list1 = new ArrayList<>();
		list1.add(true);

		if (list1.get(0)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(testIntegerPutByteArray());
		System.out.println(testIntegerPutByteArray2());
		System.out.println(testArrayLength());
		System.out.println(testArrayToString());
		System.out.println(testMultiDimArrayGetClass());
		System.out.println(testMultipleInstanceOf());
		System.out.println(testAllPrimitiveArrayTypes());
		System.out.println(test_arrayGetSuperclass());
	}
}
