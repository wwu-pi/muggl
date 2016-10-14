package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

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
		Globals.getInst().changeLogLevel(Level.ALL);
		Globals.getInst().parserLogger.setLevel(Level.WARN);
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
	public final void testApplicationMugglExecuteCounting()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.CountWordLength.METHOD_counting,
				MethodType.methodType(long.class, int.class), (Object[]) new Integer[] { 2 });
	}

	/**
	 * Test passing a String as Argument. Will be converted to Objectref from StringCache by MethodRunnerHelper
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws InterruptedException
	 */
	@Test
	public final void testApplicationPassStringObjectref()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(11,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.CountWordLength.METHOD_StringTest,
						MethodType.methodType(int.class, String.class), (Object[]) new String[] { "es wird gut" }));
	}
}
