package test.papers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * This class has been generated by Muggl for the automated testing of method
 * test.papers.Paper200809.gcd(int m, int n).
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
 * Time Limit:                  1 minutes
 * Maximum loop cycles to take: 200
 * Maximum instructions before
 * finding a new solution:     10000
 *
 * Execution has been aborted before it was finished. The time limit has been reached.
 * It might be possible to get more test cases by having less restrictive abortion
 * criteria.
 * 
 * The total number of solutions found was 29. After deleting redundancy and
 * removing unnecessary solutions, 29 distinct test cases were found.
 * By eliminating solutions based on their contribution to the
 * def-use chain and control graph edge coverage
 * the total number of solutions could be reduced by 26 to the final number of 3 test cases.
 * 
 * Covered def-use chains:		7 of 7
 * Covered control graph edges:	37 of 44
 * 
 * This file has been generated on Tuesday, 26 August, 2008 9:29 AM.
 * 
 * @author Muggl 1.00 Alpha (unreleased)
 */
public class Gcd {
	// Fields for test parameters and expected return values.
	private int int1;
	private int int3;
	private int int5;
	private int int0;
	private int intm1;

	/**
	 * Set up the unit test by initializing the fields to the desired values.
	 */
	@Before public void setUp() {
		this.int1 = 1;
		this.int3 = 3;
		this.int5 = 5;
		this.int0 = 0;
		this.intm1 = -1;
	}

	/**
	 * Run the tests on test.papers.Paper200809.gcd(int m, int n).
	 */
	@Test public void testIt() {
		assertEquals(this.int1, test.papers.Paper200809.gcd(this.int3, this.int5));
		try {
			test.papers.Paper200809.gcd(this.int0, this.intm1);
			fail("Expected a java.lang.IllegalArgumentException to be thrown.");
		} catch (java.lang.IllegalArgumentException e) {
			// Do nothing - this is what we expect to happen!
		}
		try {
			test.papers.Paper200809.gcd(this.intm1, this.int0); // In this solution, not all parameters are used (int n is unused).
			fail("Expected a java.lang.IllegalArgumentException to be thrown.");
		} catch (java.lang.IllegalArgumentException e) {
			// Do nothing - this is what we expect to happen!
		}
	}

	/**
	 * Invoke JUnit to run the unit tests.
	 * @param args Command line arguments, which are ignored here. Just supply null.
	 */
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main("test.papers.Gcd");
	}

}