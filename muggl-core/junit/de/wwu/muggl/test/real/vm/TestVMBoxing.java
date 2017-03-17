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

/**
 * 
 * @author Max Schulze
 *
 */
public class TestVMBoxing extends TestSkeleton {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.DEBUG);
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

	@Test
	public final void testBoxBooleanPutField()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Boolean) true,
				(Boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxbooleanField",
						MethodType.methodType(Boolean.class, Boolean.class), (Object[]) new Boolean[] { true }));
	}

	@Test
	public final void testBoxBooleanGetField()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Boolean) false,
				(Boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "returnInitiatedField",
						MethodType.methodType(Boolean.class), (Object[]) new Object[] {}));
	}

	@Test
	public final void testBoxBooleanPutField2()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((boolean) true,
				(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxbooleanField2",
						MethodType.methodType(boolean.class, boolean.class), (Object[]) new Boolean[] { true }));
	}

	@Test
	@Category(NotYetSupported.class)
	public final void testBoxBooleanGetField2()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((boolean) false,
				(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "returnInitiatedField2",
						MethodType.methodType(boolean.class), (Object[]) new Object[] {}));
	}

	@Test
	@Category(NotYetSupported.class)
	public final void testBoxBooleanGetFieldWrapped()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((boolean) false,
				(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "returnInitiatedFieldWrapped",
						MethodType.methodType(boolean.class), (Object[]) new Object[] {}));
	}

	@Test
	public final void testBoxBooleanGetFieldRaw()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((boolean) false,
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "returnInitiatedFieldRaw",
						MethodType.methodType(boolean.class), (Object[]) new Object[] {}));
	}

	@Test
	public final void testBugObjectReturn() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 2,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "putReturnObj",
						MethodType.methodType(Object.class, Object.class), (Object[]) new Integer[] { 2 }));

	}

	@Test
	public final void testBugReturnCast2() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 3,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "putReturnObj2",
						MethodType.methodType(Integer.class, Integer.class), (Object[]) new Integer[] { 3 }));

	}

	@Test
	public final void testBugReturnCast3() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 4,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "putReturnObj3",
						MethodType.methodType(Integer.class, Integer.class), (Object[]) new Integer[] { 4 }));

	}

	@Test
	public final void testBugReturnCast4() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 4,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "putReturnObj4",
						MethodType.methodType(Integer.class, Integer.class), (Object[]) new Integer[] { 4 }));

	}

	@Test
	public final void testBugIntegerBoxing() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 8,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "IntegerBoxing",
						MethodType.methodType(Integer.class, Integer.class), (Object[]) new Integer[] { 4 }));
	}

	@Test
	public final void testBugBoxingBoolean() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue("boxing boolean doesn't work",
				(Boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "BooleanBoxing",
						MethodType.methodType(Boolean.class, boolean.class), (Object[]) new Boolean[] { true }));

	}

	@Test
	public final void testBoolean1() throws ClassFileException, InitializationException, InterruptedException {
		// doing XOR!
		assertTrue("boolean test",
				(Boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "booleanTest2",
						MethodType.methodType(boolean.class, boolean.class), (Object[]) new Boolean[] { false }));
	}

	@Test
	public final void testBugintNoBoxing() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((int) 8,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "intNoBoxing",
						MethodType.methodType(int.class, int.class), (Object[]) new Integer[] { 4 }));

	}

	@Test
	public final void testPrinting() throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "testPrinting");

	}

	// === Boxing Conversion ===
	@Test
	public final void boxboolean() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("true",
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxboolean",
						MethodType.methodType(String.class), null));

	}

	@Test
	public final void boxBoolean() throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxBoolean",
				MethodType.methodType(String.class, Boolean.class), (Object[]) new Boolean[] { true });

	}

	@Test
	public final void boxBoolean2() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(false,
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxBoolean2",
						MethodType.methodType(boolean.class, boolean.class), (Object[]) new Boolean[] { false }));

	}

	@Test
	public final void boxBooleanObj() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("true",
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxbooleanObj",
						MethodType.methodType(String.class), null));

	}

	@Test
	public final void boxPlaceholderBoolean() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("true",
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxPlaceholder",
						MethodType.methodType(String.class, Boolean.class), (Object[]) new Boolean[] { true }));

	}

	@Test
	public final void boxPlaceholderChar() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("b",
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxPlaceholderChar",
						MethodType.methodType(String.class, char.class), (Object[]) new Character[] { 'b' }));

	}

	@Test
	public final void boxPlaceholderInt() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("599",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxPlaceholderint",
						MethodType.methodType(String.class), null));

	}

	@Test
	public final void boxintobject() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("340",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxint",
						MethodType.methodType(String.class), null));

	}

}
