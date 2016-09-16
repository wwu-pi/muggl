package de.wwu.muggl.test.real.vm;

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
 * Executes different test related to synchronized keywords of methods, and synchronized(object) {} blocks in Code.
 * 
 * @author Max Schulze
 *
 */
public class TestMonitorSuite {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.TRACE);
		Globals.getInst().parserLogger.setLevel(Level.ERROR);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		classLoader = new MugglClassLoader(new String[] { "./", "./junit-res/" });
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testMonitorExecuteStaticFlag()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.SynchronizedExecute.class.getCanonicalName(),
				"executeStaticFlag");

	}

	@Test
	public final void testMonitorExecuteStaticInstruction()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.SynchronizedExecute.class.getCanonicalName(),
				"executeStaticInstruction");

	}

	@Test
	public final void testMonitorExecuteInstanceFlagWrapper()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.SynchronizedExecute.class.getCanonicalName(),
				"executeInstanceFlagWrapper");

	}

	@Test
	public final void testMonitorExecuteInstanceInstructionWrapper()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.SynchronizedExecute.class.getCanonicalName(),
				"executeInstanceInstructionWrapper");

	}

	@Test
	public final void testMonitorExecuteInstanceInstructionTwiceWrapper()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.SynchronizedExecute.class.getCanonicalName(),
				"executeInstanceInstructionTwiceWrapper");

	}

}
