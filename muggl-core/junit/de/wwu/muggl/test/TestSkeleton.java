package de.wwu.muggl.test;

import org.apache.log4j.Level;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;

import de.wwu.muggl.configuration.Globals;

/**
 * Make all tests inherit from this skeleton, so as to impose general rules on timeouts, logging, etc... for automated
 * testing of the full suite
 * 
 * @author Max Schulze
 *
 */
public class TestSkeleton {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(200);

	/**
	 * subclasses should not change loglevel if set (for CI tests...).
	 * 
	 * Set this to false if you're testing locally, but always commit true for CI
	 */
	public final static boolean isForbiddenChangingLogLevel = true;

	public final static String[] mugglClassLoaderPaths = new String[] { "./", "./junit-res/" };

	@BeforeClass
	public static void setUpBeforeClass1() throws Exception {
		Globals.getInst().changeLogLevel(Level.ERROR);
		Globals.getInst().parserLogger.setLevel(Level.ERROR);
	}

}
