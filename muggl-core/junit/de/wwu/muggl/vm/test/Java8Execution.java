package de.wwu.muggl.vm.test;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class Java8Execution {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.INFO);
		Globals.getInst().execLogger.setLevel(Level.ALL);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private MugglClassLoader classLoader;

	@Before
	public void setUp() throws Exception {
		classLoader = new MugglClassLoader(new String[] { "./", "./junit-res/" });
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testApplicationMugglClassLoaderStringMethod()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethod(classLoader, "binary.openjdk.one.eight.zero.ninetyone.CountWordLength",
				"counting", "(I)J", (Object[]) new Integer[] { 2 });
	}
}
