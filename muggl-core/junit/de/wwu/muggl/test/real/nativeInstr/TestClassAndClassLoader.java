package de.wwu.muggl.test.real.nativeInstr;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.hamcrest.CoreMatchers;
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
public class TestClassAndClassLoader {
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

	// @Test // for now endless loop
	public final void test_GetClassAnnotations()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("@de.wwu.muggl.binaryTestSuite.nativeInstr.MyAnnotation()",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassAnnotations", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetClassDeclaredConstructors()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("public de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader()",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassDeclaredConstructors", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetClassDeclaredFields()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("serialVersionUID",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassDeclaredFields", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetClassDeclaredMethods()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("test_GetClassName",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassDeclaredMethods", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetClassInterfaces()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.io.Serializable",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassInterfaces", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetClassLoader() throws ClassFileException, InitializationException, InterruptedException {
		assertThat(
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassLoader", MethodType.methodType(String.class), null),
				CoreMatchers.startsWith("sun.misc.Launcher$AppClassLoader@"));

	}

	@Test
	public final void test_GetClassModifiers()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassModifiers", MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_GetClassName() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassName", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetClassSigners() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(-1,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassSigners", MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_GetComponentType() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("long",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetComponentType", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetDeclaredClasses()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.Testclass.class.getCanonicalName(),
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetDeclaredClasses", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetDeclaringClass()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetDeclaringClass", MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_GetProtectionDomain()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				"test_GetProtectionDomain", MethodType.methodType(String.class), null);

	}

	@Test
	public final void test_IsArrayClass() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				"test_IsArrayClass", MethodType.methodType(boolean.class), null));

	}

	@Test
	public final void test_IsInterface() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				"test_IsInterface", MethodType.methodType(boolean.class), null));
	}

	@Test
	public final void test_IsPrimitive() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				"test_IsPrimitive", MethodType.methodType(boolean.class), null));

	}

}
