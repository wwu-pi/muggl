package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestVMBoxing {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.WARN);
		Globals.getInst().parserLogger.setLevel(Level.WARN);
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
	public final void testBugObjectReturn() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 2,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "putReturnObj",
						MethodType.methodType(Object.class, Object.class).toMethodDescriptorString(),
						(Object[]) new Integer[] { 2 }));

	}

	@Test
	public final void testBugReturnCast2() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 3,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "putReturnObj2",
						MethodType.methodType(Integer.class, Integer.class).toMethodDescriptorString(),
						(Object[]) new Integer[] { 3 }));

	}

	@Test
	public final void testBugReturnCast3() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 4,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "putReturnObj3",
						MethodType.methodType(Integer.class, Integer.class).toMethodDescriptorString(),
						(Object[]) new Integer[] { 4 }));

	}

	@Test
	public final void testBugReturnCast4() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 4,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "putReturnObj4",
						MethodType.methodType(Integer.class, Integer.class).toMethodDescriptorString(),
						(Object[]) new Integer[] { 4 }));

	}

	@Test
	public final void testBugIntegerBoxing() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((Integer) 8,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "IntegerBoxing",
						MethodType.methodType(Integer.class, Integer.class).toMethodDescriptorString(),
						(Object[]) new Integer[] { 4 }));
	}

	@Test
	public final void testBugBoxingBoolean() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue("boxing boolean doesn't work",
				(Boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "BooleanBoxing",
						MethodType.methodType(Boolean.class, boolean.class).toMethodDescriptorString(),
						(Object[]) new Boolean[] { true }));

	}

	@Test
	public final void testBoolean1() throws ClassFileException, InitializationException, InterruptedException {
		// doing XOR!
		assertTrue("boolean test",
				(Boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "booleanTest2",
						MethodType.methodType(boolean.class, boolean.class).toMethodDescriptorString(),
						(Object[]) new Boolean[] { false }));
	}

	@Test
	public final void testBugintNoBoxing() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals((int) 8,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "intNoBoxing",
						MethodType.methodType(int.class, int.class).toMethodDescriptorString(),
						(Object[]) new Integer[] { 4 }));

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
						MethodType.methodType(String.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void boxBoolean() throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxBoolean",
				MethodType.methodType(String.class, Boolean.class).toMethodDescriptorString(),
				(Object[]) new Boolean[] { true });

	}

	@Test
	public final void boxBoolean2() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(false,
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxBoolean2",
						MethodType.methodType(boolean.class, boolean.class).toMethodDescriptorString(),
						(Object[]) new Boolean[] { false }));

	}

	@Test
	public final void boxBooleanObj() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("true",
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxbooleanObj",
						MethodType.methodType(String.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void boxPlaceholderBoolean() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("true",
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxPlaceholder",
						MethodType.methodType(String.class, Boolean.class).toMethodDescriptorString(),
						(Object[]) new Boolean[] { true }));

	}

	@Test
	public final void boxPlaceholderChar() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("b",
				TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxPlaceholderChar",
						MethodType.methodType(String.class, char.class).toMethodDescriptorString(),
						(Object[]) new Character[] { 'b' }));

	}

	@Test
	public final void boxPlaceholderInt() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("599",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxPlaceholderint",
						MethodType.methodType(String.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void boxintobject() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("340",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Boxing.class.getCanonicalName(), "boxint",
						MethodType.methodType(String.class).toMethodDescriptorString(), null));

	}

}
