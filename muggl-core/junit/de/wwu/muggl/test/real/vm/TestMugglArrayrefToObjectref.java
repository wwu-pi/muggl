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
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class TestMugglArrayrefToObjectref {
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
	public final void testVMArrayref() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((Integer) 5,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testObjectref,
						MethodType.methodType(int.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void testVMArrayrefInvokevirtual() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((String) "1,2,3,4,5",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testObjectref2,
						MethodType.methodType(String.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void testVMIntegerPutByteArray() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((Integer) 6,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testIntegerPutByteArray,
						MethodType.methodType(int.class).toMethodDescriptorString(), null));

	}

}
