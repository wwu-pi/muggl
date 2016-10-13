package de.wwu.muggl.test.real.nativeInstr;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

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
public class TestReflectArray {
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
	public final void test_GetArrayLength() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(3,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_GetArrayLength", MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_GetArrayElement() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(999,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_GetArrayElement", MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_SetArrayElement() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1000,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_SetArrayElement", MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_NewArray() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(0,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(), "test_NewArray",
						MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_NewMultiArray() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(4,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(),
						"test_NewMultiArray", MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_IsArrayClass() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ReflectArray.class.getCanonicalName(), "test_IsArrayClass",
				MethodType.methodType(boolean.class), null));

	}

}
