package de.wwu.muggl.test.real.vm;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class TestVMClassInitialization extends TestSkeleton {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.TRACE);
			Globals.getInst().parserLogger.setLevel(Level.ERROR);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		classLoader = new MugglClassLoader(mugglClassLoaderPaths);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetVMProperty() throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
				"tryGetProperty", MethodType.methodType(String.class), null);
	}

	// @Test
	public final void testGetVMProperties() throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertTrue("ListSystemProperties has no entries at all",
				((Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
						"listSystemProperties", MethodType.methodType(int.class), null)) > 1);
	}

	@Test
	public final void testGetVMIsBooted() throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertTrue("VM is not booted",
				(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
						"isbooted", MethodType.methodType(boolean.class), null));
	}

	@Test
	public final void testReturnValue() throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertTrue("correct return value from function",
				(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
						"testReturnValues", MethodType.methodType(boolean.class), null));
	}

}
