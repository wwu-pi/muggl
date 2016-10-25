package de.wwu.muggl.test.real.nativeMethodCalls;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestReflectiveMethodInvocation extends TestSkeleton {
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
	public final void test_invokeMethod() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("hello, world!",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.METHOD_test_invokeMethod,
						MethodType.methodType(String.class), new Object[0]));

	}

	// @Test
	public final void test_invokeMethodWithArg()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("hello, world!2",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.METHOD_test_invokeMethodWithArg,
						MethodType.methodType(String.class), new Object[0]));

	}

	// @Test
	public final void test_invokeInstanceMethodWithArg()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("hello, world!3",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.METHOD_test_invokeInstanceMethodWithArg,
						MethodType.methodType(String.class), new Object[0]));

	}

}
