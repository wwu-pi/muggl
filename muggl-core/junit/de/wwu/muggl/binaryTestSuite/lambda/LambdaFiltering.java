package de.wwu.muggl.binaryTestSuite.lambda;

import java.util.ArrayList;
import java.util.List;

/**
 * compiled from https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html.
 * 
 * Showcase lambda without streams
 * 
 * @author Max Schulze
 *
 */
public class LambdaFiltering {
	static List<Person> roster = new ArrayList<>();
	static {
		roster.add(new Person("Hans", 26, 51000));
		roster.add(new Person("Mary", 24, 52000));
		roster.add(new Person("Rose", 34, 78000));
	}

	// functional interface
	interface CheckPerson {
		boolean test(Person p);
	}

	public static int countPersons(CheckPerson tester) {
		int matching = 0;
		for (Person p : roster) {
			if (tester.test(p)) {
				matching++;
			}
		}
		return matching;
	}

	public final static String METHOD_helperExecute_countPersons = "helperExecute_countPersons";

	public static int helperExecute_countPersons(int age) {
		return countPersons((Person p) -> p.age > age && p.salary > 50000);
	}

	public static void main(String[] args) {

		System.out.println(helperExecute_countPersons(25));
	}
}
