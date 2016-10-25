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
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * This class shall test the §2.11.4 Type Conversion Instructions.
 * 
 * See also Table 2.11.1-A "Type support in the Java Virtual Machine instruction set"
 * 
 * @author Max Schulze
 *
 */
public class TestNarrowingWideningInstructions extends TestSkeleton {
	MugglClassLoader classLoader;

	// how much epsilon is permitted in double comparisons?
	private static final double epsilon = 1e-10;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.ERROR);
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
	public final void test_instr_d2f() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((float) 4,
				(float) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "d2f",
						MethodType.methodType(float.class), null),
				epsilon);

	}

	@Test
	public final void test_instr_d2i() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((int) 3,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "d2i",
						MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_instr_d2l() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((long) 3,
				(long) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "d2l",
						MethodType.methodType(long.class), null));

	}

	@Test
	public final void test_impl_d2s() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((short) 3,
				(short) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "d2s",
						MethodType.methodType(short.class), null));

	}

	@Test
	public final void test_instr_f2d() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((double) 444444,
				(double) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "f2d",
						MethodType.methodType(double.class), null),
				epsilon);

	}

	@Test
	public final void test_instr_f2i() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((int) 3,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "f2i",
						MethodType.methodType(int.class), null));

	}

	@Test
	public final void test_instr_f2l() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((long) 3,
				(long) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "f2l",
						MethodType.methodType(long.class), null));

	}

	@Test
	public final void test_instr_i2b() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((byte) 4,
				(byte) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "i2b",
						MethodType.methodType(byte.class), null));

	}

	@Test
	public final void test_instr_i2c() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((char) 49,
				(char) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "i2c",
						MethodType.methodType(char.class), null));

	}

	@Test
	public final void test_instr_i2d() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((double) 32443,
				(double) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "i2d",
						MethodType.methodType(double.class), null),
				epsilon);

	}

	@Test
	public final void test_instr_i2f() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((float) 32445,
				(float) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "i2f",
						MethodType.methodType(float.class), null),
				epsilon);

	}

	@Test
	public final void test_instr_i2l() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((long) 32550,
				(long) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "i2l",
						MethodType.methodType(long.class), null));

	}

	@Test
	public final void test_instr_i2s() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((short) 4,
				(short) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "i2s",
						MethodType.methodType(short.class), null));

	}

	@Test
	public final void test_impl_l2b() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((byte) -7,
				(byte) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "l2b",
						MethodType.methodType(byte.class), null));

	}

	@Test
	public final void test_instr_l2d() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((double) 499999,
				(double) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "l2d",
						MethodType.methodType(double.class), null),
				epsilon);

	}

	@Test
	public final void test_instr_l2f() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((float) 48888,
				(float) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "l2f",
						MethodType.methodType(float.class), null),
				epsilon);

	}

	@Test
	public final void test_instr_l2i() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((int) 3245,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.NarrowingWideningConversions.class.getCanonicalName(), "l2i",
						MethodType.methodType(int.class), null));

	}

}
