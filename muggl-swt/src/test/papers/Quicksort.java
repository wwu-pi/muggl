package test.papers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * This class has been generated by Muggl for the automated testing of method
 * test.papers.Paper200809.quicksort(int[] data).
 * Test cases have been computed using the symbolic execution of Muggl. Muggl
 * is a tool for the fully automated generation of test cases by analysing a
 * program's byte code. It aims at testing any possible flow through the program's
 * code rather than "guessing" required test cases, as a human would do.
 * Refer to http://www.wi.uni-muenster.de/pi/personal/majchrzak.php for more
 * information or contact the author at tim.majchrzak@wi.uni-muenster.de.
 * 
 * Executing the method main(null) will invoke JUnit (if it is in the class path).
 * The methods for setting up the test and for running the tests have also been
 * annotated.
 * 
 * Important settings for this run:
 * Search algorithm:            iterative deepening depth first
 * Time Limit:                  30 seconds
 * Maximum loop cycles to take: 200
 * Maximum instructions before
 * finding a new solution:     100000
 *
 * Execution has been aborted before it was finished. The time limit has been reached.
 * It might be possible to get more test cases by having less restrictive abortion
 * criteria.
 * 
 * The maximum number of loops was reached at least one time. Setting a higher
 * number of maximum loops to reach before backtracking might lead to a higher
 * number of solutions found.
 * 
 * The total number of solutions found was 20. After deleting redundancy and
 * removing unnecessary solutions, 20 distinct test cases were found.
 * By eliminating solutions based on their contribution to the
 * def-use chain and control graph edge coverage
 * the total number of solutions could be reduced by 16 to the final number of 4 test cases.
 * 
 * Covered def-use chains:		19 of 21
 * Covered control graph edges:	96 of 125
 * 
 * This file has been generated on Monday, 01 September, 2008 2:14 PM.
 * 
 * @author Muggl 1.00 Alpha (unreleased)
 */
public class Quicksort {
	// Fields for test parameters and expected return values.
	private int[] returnedArray0 = {-1,0,1};
	private int[] returnedArray1 = {0,0};
	private test.papers.Paper200809 testedClass;
	private int[] array0 = {-1,0,1};
	private int[] array1 = {0,0};
	private int[] array2 = new int[0];
	private int[] array3 = null;

	/**
	 * Set up the unit test by initializing the fields to the desired values.
	 */
	@Before public void setUp() {
		this.testedClass = new test.papers.Paper200809();
	}

	/**
	 * Run the tests on test.papers.Paper200809.quicksort(int[] data).
	 */
	@Test public void testIt() {
		assertArrayEquals(this.returnedArray0, this.testedClass.quicksort(this.array0));
		assertArrayEquals(this.returnedArray1, this.testedClass.quicksort(this.array1));
		try {
			this.testedClass.quicksort(this.array2);
			fail("Expected a java.lang.ArrayIndexOutOfBoundsException to be thrown.");
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			// Do nothing - this is what we expect to happen!
		}
		try {
			this.testedClass.quicksort(this.array3);
			fail("Expected a java.lang.NullPointerException to be thrown.");
		} catch (java.lang.NullPointerException e) {
			// Do nothing - this is what we expect to happen!
		}
	}

	/**
	 * Invoke JUnit to run the unit tests.
	 * @param args Command line arguments, which are ignored here. Just supply null.
	 */
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main("test.papers.Quicksort");
	}

}