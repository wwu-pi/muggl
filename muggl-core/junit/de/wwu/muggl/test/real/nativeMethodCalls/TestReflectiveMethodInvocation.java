package de.wwu.muggl.test.real.nativeMethodCalls;

import de.wwu.muggl.NotYetSupported;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import org.apache.log4j.Level;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.lang.invoke.MethodType;

import static org.junit.Assert.assertEquals;

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

	@Test // läuft
	public final void test_invokeMethod() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("hello, world!",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.METHOD_test_invokeMethod,
						MethodType.methodType(String.class), new Object[0]));

	}

	@Test // läuft
	public final void test_invokeMethodWithArg()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("hello, world!2",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.METHOD_test_invokeMethodWithArg,
						MethodType.methodType(String.class), new Object[0]));

	}

	@Test // läuft
	public final void test_invokeInstanceMethodWithArg()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("hello, world!3",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.METHOD_test_invokeInstanceMethodWithArg,
						MethodType.methodType(String.class), new Object[0]));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_invokeInstanceMethodWithArgByLookup()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("hello, world!3",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ReflectiveMethodInvocation.METHOD_test_invokeInstanceMethodWithArgByNameLookup,
						MethodType.methodType(String.class), new Object[0]));

	}

}
