package de.wwu.muggl.test.real.instructions;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestInvokeVirtual {
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
	public final void testApplicationMugglVMInvokeVirtualExecution1()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.MethodHandleTest.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.invokevirtual.MethodHandleTest.METHOD_findStaticInvokeExact, "()V", null);

	}

	@Test
	public final void testApplicationMugglVMInvokeVirtualExecution2()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.MethodHandleTest.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.invokevirtual.MethodHandleTest.METHOD_findVirtualInvokeExact, "()V", null);

	}

	@Test
	public final void testApplicationMugglVMInvokeVirtualExecution3()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.MethodHandleTest.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.invokevirtual.MethodHandleTest.METHOD_testWithBootstrap, "()V", null);

	}

}
