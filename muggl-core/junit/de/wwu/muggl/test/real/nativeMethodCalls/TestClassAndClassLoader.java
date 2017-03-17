package de.wwu.muggl.test.real.nativeMethodCalls;

import de.wwu.muggl.NotYetSupported;
import de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.Reflection;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import org.apache.log4j.Level;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestClassAndClassLoader extends TestSkeleton {
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

	// @Test
	public final void test_MethodCreatorHelper() throws ClassFileException, InitializationException,
			NoSuchMethodException, IllegalAccessException, ExecutionException {
		MethodType mt = MethodType.methodType(String.class, int.class);
		ClassFile classF = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName());
		Method method = classF.getMethodByNameAndDescriptor("test_MethodWithParamsAndException",
				mt.toMethodDescriptorString());
		@SuppressWarnings("unused")
		Application appli = new Application(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(), method);

		Objectref method2 = Reflection.newMethod(method, false);

		java.lang.reflect.Method methodRef = de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class
				.getMethod("test_MethodWithParamsAndException", mt.parameterArray());

		Objectref returnTypeClass = (Objectref) method2
				.getField(method2.getInitializedClass().getClassFile().getFieldByName("returnType"));

		ClassFile mirror = returnTypeClass.getMirrorMuggl();

		assertEquals(methodRef.getReturnType().getName(), mirror.getName());

	}

	// //@Test // for now endless loop
	public final void test_GetClassAnnotations()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("@de.wwu.muggl.binaryTestSuite.nativeInstr.MyAnnotation()",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassAnnotations", MethodType.methodType(String.class), null));

	}

	// @Test
	public final void test_GetClassDeclaredConstructors()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("public de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader()",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassDeclaredConstructors,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_GetClassDeclaredFields()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("mySuperSpecialField",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassDeclaredFields,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_GetClassDeclaredFieldsCount()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(37,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassDeclaredFieldsCount,
						MethodType.methodType(int.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_GetClassPrimitiveNames()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("booleanbyteshortcharintlongfloatdoubleObjectvoid",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassPrimitiveNames,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_Subclass() throws ClassFileException, InitializationException, InterruptedException {

		ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName() + "$Testclass",
				true);
		assertEquals(
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName() + "$Testclass",
				classFile.getName());

	}

	@Test // läuft
	public final void test_GetClassDeclaredMethods()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("test_GetClassForName",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassDeclaredMethods,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_GetClassArrayComponentType()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.lang.Class",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassArrayComponentType,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_GetClassInterfaces()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.io.Serializable",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassInterfaces,
						MethodType.methodType(String.class), null));
	}

	// @Test
	public final void test_GetClassLoader() throws ClassFileException, InitializationException, InterruptedException {
		assertThat(
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						"test_GetClassLoader", MethodType.methodType(String.class), null),
				CoreMatchers.startsWith("sun.misc.Launcher$AppClassLoader@"));

	}

	@Test // läuft
	public final void test_GetClassModifiers()
			throws ClassFileException, InitializationException, InterruptedException {

		int reflectiveAccessFlags = (int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassModifiers,
				MethodType.methodType(int.class), null);

		// muggl does return the full range
		assertEquals(ClassFile.ACC_PUBLIC | ClassFile.ACC_SUPER, reflectiveAccessFlags);

		// but java reflection only uses a subset
		assertEquals(ClassAndClassLoader.class.getModifiers(), reflectiveAccessFlags & Modifier.classModifiers());

	}

	@Test // läuft
	public final void test_GetClassName() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassName,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_GetClassNameForPrimitive()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(int.class.getName(),
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassNameForPrimitive,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_GetClassNameObj() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.lang.Integer",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassNameObj,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_GetClassForName() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.lang.Object",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassForName,
						MethodType.methodType(String.class), null));

	}

	// @Test
	public final void test_GetClassForNameWithException()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("success",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassForNameWithException,
						MethodType.methodType(String.class), null));

	}

	// @Test
	public final void test_GetClassSigners() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(-1,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassSigners,
						MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_GetComponentType() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("long",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetComponentType,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_GetComponentType2()
			throws ClassFileException, InitializationException, InterruptedException {

		assertEquals(3,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetComponentType2,
						MethodType.methodType(int.class), null));

	}

	// @Test // tricky one
	public final void test_GetComponentTypeIdentity()
			throws ClassFileException, InitializationException, InterruptedException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetComponentTypeIdentity,
				MethodType.methodType(boolean.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_GetGetClassPrimitive()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("char",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetGetClassPrimitive,
						MethodType.methodType(String.class), null));
	}

	@Test // läuft
	public final void test_GetDeclaredClasses()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName() + "$Testclass",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetDeclaredClasses,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_GetDeclaringClass()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetDeclaringClass,
						MethodType.methodType(String.class), null));

	}

	@Test // läuft
	public final void test_GetDeclaringClassOnPrimitive()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("null",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetDeclaringClassOnPrimitive,
						MethodType.methodType(String.class), null));

	}

	// @Test
	public final void test_GetProtectionDomain()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetProtectionDomain,
				MethodType.methodType(String.class), null);

	}

	@Test // läuft
	public final void test_IsArrayClass() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue(!(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_IsArrayClass,
				MethodType.methodType(boolean.class), null));

	}

	@Test // läuft
	public final void test_IsInterface() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_IsInterface,
						MethodType.methodType(int.class), null));
	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_IsPrimitive() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(90,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_CountPrimitive,
						MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_PrimitiveClassesReferenceEqual()
			throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_PrimitiveClassesReferenceEqual,
				MethodType.methodType(boolean.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_IsPrimitiveInstances()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(64,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_CountPrimitiveInstances,
						MethodType.methodType(int.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_isInstance() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_isInstance,
				MethodType.methodType(boolean.class), null));

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void test_isAssignableFrom() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_isAssignableFrom,
						MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_ObjectClass() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(-1,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_ObjectClass,
						MethodType.methodType(int.class), null));

	}

	@Test // läuft
	public final void test_GetClassAccessFlags()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(49,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.nativeInstr.ClassAndClassLoader.METHOD_test_GetClassAccessFlags,
						MethodType.methodType(int.class), null));

	}

}
