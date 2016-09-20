package de.wwu.muggl.binaryTestSuite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Very simple test for lambdas and streams.
 * @author max
 *
 */
public class CountWordLength {

	public static long counting(int filterLength) {
		// this internally uses a HashMap
		//Collection<String> myList = Arrays.asList("Hello", "Java");
		Collection<String> myList = new ArrayList<String>() {{ add("Hello"); add("Java");}};
		
		long countLongStrings = myList.stream().filter(element -> element.length() > filterLength).count();

		return countLongStrings;
	}

	public static void main(String[] args) {
		System.out.println(counting(2));
	}
}
