package de.wwu.muggl.test.real.instructions;

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
public class TestSharedSecrets extends TestSkeleton {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.TRACE);
			Globals.getInst().parserLogger.setLevel(Level.WARN);
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
	public final void testGetSuperclass() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.lang.Enum",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(),
						"getMySuperClass", MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void testIterateValues() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(),
						"iterateValues", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void testgetSuperSuperclass()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.lang.Object",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(),
						"getMySuperSuperClass", MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void testgetObjectSuperclass()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("npe as expected",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(),
						"getObjectSuperclass", MethodType.methodType(String.class), null));

	}

	// nota: this involves sun.reflect.NativeMethodAccessorImpl.invoke0
	@Test // läuft
	public final void testGetValuesShared() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(), "getValues",
						MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void testClassIsEnum() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(), "getClassIsEnum",
				MethodType.methodType(boolean.class), null));

	}

	@Test // läuft
	public final void testGetUnsafe() throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(), "unsafer");

	}

}
