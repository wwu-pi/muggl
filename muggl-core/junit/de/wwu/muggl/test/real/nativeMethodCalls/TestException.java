package de.wwu.muggl.test.real.nativeMethodCalls;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
public class TestException extends TestSkeleton {
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

	@Test // Known-to-fail: PC is set incorrectly.
	@Ignore
	public final void testExceptionTable() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("",(String)TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ExceptionStackTrace.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ExceptionStackTrace.METHOD_testExceptionTable,
						MethodType.methodType(String.class), null));

	}

	@Test
	public final void testExceptionStackTrace() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.lang.IllegalArgumentException: no arguments at all...",
				(String)TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ExceptionStackTrace.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ExceptionStackTrace.METHOD_testExceptionStackTrace,
						MethodType.methodType(String.class), null));

	}
}
