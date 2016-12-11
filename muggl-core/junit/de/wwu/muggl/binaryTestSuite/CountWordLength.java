package de.wwu.muggl.binaryTestSuite;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Very simple test for lambdas and streams.
 * 
 * @author max
 *
 */
public class CountWordLength {
	public String teststring = "asdfghjkl";

	public final static String METHOD_counting = "counting";

	public static String returnConstantValue() {
		return METHOD_counting;
	}

	public final static String METHOD_returnStaticFieldFriendClass = "returnStaticFieldFriendClass";

	public boolean returnStaticFieldFriendClass() {
		return Boxing.test3;
	}

	static Collection<Integer> myList = new ArrayList<Integer>() {
		private static final long serialVersionUID = -1686817071337627569L;

		{
			add(4);
			add(5);
		}
	};

	public static long counting(int filterLength) {
		// this internally uses a HashMap
		// Collection<String> myList = Arrays.asList("Hello", "Java");

		long countLongStrings = myList.stream().filter(element -> element > filterLength).count();

		return countLongStrings;
	}

	public static Boolean filter(int filterLength, Integer entry) {
		return entry > filterLength;
	}

	public static final String METHOD_countingreflective = "countingReflective";

	public static int countingReflective(int filterLength) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		int countLongStrings = 0;
		for (Integer entry : myList) {
			if ((boolean) CountWordLength.class.getMethod("filter", int.class, Integer.class).invoke(null,
					new Object[] { filterLength, entry })) {
				countLongStrings++;
			}
		}
		return countLongStrings;
	}

	public final static String METHOD_StringTest = "countStringLength_ParameterStringTest";

	/**
	 * The corresponding junit test demonstrates how to pass a String reference to the MugglVM
	 * 
	 * @param testin
	 * @return length of the String
	 */
	public static int countStringLength_ParameterStringTest(String testin) {
		return testin.length();
	}

	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		System.out.println(counting(2));
		System.out.println(countingReflective(2));
	}
}
