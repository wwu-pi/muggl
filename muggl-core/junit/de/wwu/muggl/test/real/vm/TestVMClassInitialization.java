package de.wwu.muggl.test.real.vm;

import de.wwu.muggl.NotYetSupported;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import org.apache.log4j.Level;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.lang.invoke.MethodType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	@Test
	public final void testGetVMProperties() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue("ListSystemProperties has no entries at all",
				((Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
						"listSystemProperties", MethodType.methodType(int.class), null)) > 1);
	}

	@Test // so yeah, currently, they are no there.
	@Category(NotYetSupported.class)
	public final void testGetVMPropertiesMandatory()
			throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.METHOD_MANDATORYPROPS,
				MethodType.methodType(boolean.class), null));
	}

	@Test // closely related to testGetVMPropertiesMandatory,except it also tests for doPrivileged
	public final void testApplicationMugglVMTestGetSystemProperty()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(99,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.METHOD_testGetSystemProperty,
						MethodType.methodType(int.class), null));

	}

	
	@Test // closely related to testGetVMPropertiesMandatory,except it also tests for doPrivileged
	public final void testApplicationMugglVMTestDoPrivileged()
			throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.METHOD_testDoPrivileged,
						MethodType.methodType(boolean.class), null));

	}

	@Test
	public final void testGetVMIsBooted() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue("VM is not booted",
				(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
						"isbooted", MethodType.methodType(boolean.class), null));
	}

	@Test
	public final void testReturnValue() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue("correct return value from function",
				(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(),
						"testReturnValues", MethodType.methodType(boolean.class), null));
	}

}
