package de.wwu.muggl.binaryTestSuite;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Very simple test for lambdas and streams.
 * 
 * @author max
 *
 */
public class CountWordLength {
	public final static String METHOD_counting = "counting";
	public final static MethodType METHOD_counting_sig = MethodType.methodType(long.class, int.class);
	public static long counting(int filterLength) {
		// this internally uses a HashMap
		// Collection<String> myList = Arrays.asList("Hello", "Java");
		Collection<String> myList = new ArrayList<String>() {
			{
				add("Hello");
				add("Java");
			}
		};

		long countLongStrings = myList.stream().filter(element -> element.length() > filterLength).count();

		return countLongStrings;
	}

	public final static String METHOD_StringTest = "countStringLength_ParameterStringTest";
	/**
	 * The corresponding junit test demonstrates how to pass a String reference to the MugglVM
	 * @param testin
	 * @return length of the String
	 */
	public static int countStringLength_ParameterStringTest(String testin) {
		return testin.length();
	}

	public static void main(String[] args) {
		System.out.println(counting(2));
	}
}
