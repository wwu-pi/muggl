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
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestReflectArray extends TestSkeleton {
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
	public final void test_GetArrayLength() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(3,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_GetArrayLength", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_GetArrayElement() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(999,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_GetArrayElement", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_GetArrayElementMultiple()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(9,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_GetArrayElementMultiple", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_SetArrayElement() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1000,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_SetArrayElement", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_SetArrayElementMultiple()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(7,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_SetArrayElementMultipleIntegerBased", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_SetArrayElementShort()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_SetArrayElementShort", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_SetArrayElementChar()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_SetArrayElementChar", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_NewArray() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(0,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(), "test_NewArray",
						MethodType.methodType(int.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_NewMultiArray() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(3,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_NewMultiArray", MethodType.methodType(int.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_IsArrayClass() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(), "test_IsArrayClass",
				MethodType.methodType(boolean.class), null));

	}
	
	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_ArrayCopyOf() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(), 
				de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.METHOD_test_ArrayCopyOf,
				MethodType.methodType(boolean.class), null));

	}
	
	@Test // läuft
	public final void test_ArrayCopyOfRange() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(), 
				de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.METHOD_test_ArrayCopyOfRange,
				MethodType.methodType(boolean.class), null));

	}

}
