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
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * This class shall test the JVM spec Table 2.11.1-A "Type support in the Java Virtual Machine instruction set" on
 * implicit instructions (via integer conversion)
 * 
 * @author Max Schulze
 *
 */
public class TestInstructionsTypeSupport {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.WARN);
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
	public final void testVMbyte_imul() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((byte) 6,
				(byte) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.InstructionsType.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.InstructionsType.METHOD_byte_imul,
						MethodType.methodType(byte.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void testVMchar_iinc() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals('1',
				(char) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.InstructionsType.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.InstructionsType.METHOD_char_iinc,
						MethodType.methodType(char.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void testVMshort_ineg() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals(-48,
				(short) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.InstructionsType.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.InstructionsType.METHOD_short_ineg,
						MethodType.methodType(short.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void testVMshort_bytecompifeq() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertTrue(!(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.InstructionsType.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.InstructionsType.METHOD_bytecompifeq,
				MethodType.methodType(boolean.class).toMethodDescriptorString(), null));

	}
	
	@Test
	public final void test_byteStoreField() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertTrue(!(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.InstructionsType.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.InstructionsType.METHOD_byteStoreField,
				MethodType.methodType(boolean.class).toMethodDescriptorString(), null));

	}
	
	@Test
	public final void testbooleanInternalInt() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertTrue(!(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.InstructionsType.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.InstructionsType.METHOD_booleanInternalInt,
				MethodType.methodType(boolean.class).toMethodDescriptorString(), null));

	}

}
