package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;
import org.apache.log4j.Level;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class TestMugglArrayrefToObjectref {
	MugglClassLoader classLoader;
	@Rule
	public Timeout globalTimeout = Timeout.seconds(10); // 10 seconds max per method tested

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
	public final void testArrayLength() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((Integer) 5,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testArrayLength,
						MethodType.methodType(int.class), null));

	}

	@Test
	public final void testBooleanTreatedAsInt() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testBooleanTreatedAsInt,
				MethodType.methodType(boolean.class), null));

	}

	@Test
	public final void testArrayToString() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {
		assertThat((String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testArrayToString,
				MethodType.methodType(String.class), null), CoreMatchers.startsWith("[I@"));

	}

	@Test
	public final void testVMArrayrefInstanceof() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testArrayInstanceOf,
				MethodType.methodType(boolean.class), null));

	}

	@Test
	public final void testMultiDimArrayGetClass() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((String) "[[I",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testMultiDimArrayGetClass,
						MethodType.methodType(String.class), null));

	}

	@Test
	public final void testarrayGetSuperclass() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((String) "java.lang.Object",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_arrayGetSuperclass,
						MethodType.methodType(String.class), null));

	}

	@Test
	public final void testarrayClone() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertThat((String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_arrayClone, MethodType.methodType(String.class),
				null), CoreMatchers.startsWith("3[I@"));

	}

	@Test
	public final void testStringrefClassName() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((String) "[I blablabla",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testStringrefClassName,
						MethodType.methodType(String.class), null));

	}

	// @Test //rather advanced Test
	public final void testArrayClassEquivalence() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testArrayClassEquivalence,
				MethodType.methodType(boolean.class), null));

	}

	// @Test//rather advanced Test, currently failing in if_acmpne expecting reference values (and gets raw types)
	public final void testArraySubarrayClone() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testArraySubarrayClone,
				MethodType.methodType(boolean.class), null));

	}

	@Test
	public final void testMultipleInstanceOfArray() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		// assertEquals((String) "[[I[[Ljava.lang.Integer;[Ljava.lang.Object;[[Ljava.lang.Integer;",
		assertEquals((String) "[[I[[[Ljava.lang.Object;[[I",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testMultipleInstanceOf,
						MethodType.methodType(String.class), null));

	}

	@Test
	public final void testVMArrayrefMultiplePrimitives() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((String) "[B[C[D[F[J[S[Z[I",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testAllPrimitiveArrayTypes,
						MethodType.methodType(String.class), null));

	}

	@Test
	public final void testVMIntegerPutByteArray() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((Integer) 6,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testIntegerPutByteArray,
						MethodType.methodType(int.class), null));

	}

	@Test
	public final void testVMIntegerPutByteArray2() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((Integer) 6,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testIntegerPutByteArray2,
						MethodType.methodType(int.class), null));

	}

	@Test
	public final void testVMArrayRefObjectrefBoolean() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_ArrayRefObjectrefBoolean,
				MethodType.methodType(boolean.class), null));

	}
}
