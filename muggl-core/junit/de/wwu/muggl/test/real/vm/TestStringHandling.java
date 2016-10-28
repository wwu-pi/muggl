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
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class TestStringHandling extends TestSkeleton {
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
	public final void Test_StringReferenceEquality() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.StringHandling.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.StringHandling.METHOD_StringReferenceEquality,
				MethodType.methodType(boolean.class), null));

	}

	@Test
	public final void Test_StringEquality() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.StringHandling.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.StringHandling.METHOD_StringEquality, MethodType.methodType(boolean.class),
				null));

	}

	@Test
	public final void Test_Substring() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.StringHandling.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.StringHandling.METHOD_Substring, MethodType.methodType(boolean.class),
				null));

	}

	@Test
	public final void Test_StartsWith() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.StringHandling.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.StringHandling.METHOD_StartsWith, MethodType.methodType(boolean.class),
				null));

	}

	// run this test with assertions on, checks correct String Handling when parsing from modified UTF-8 (as in class files)
	@Test
	public final void Test_InitLatin1() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals(2, (int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.StringHandling.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.StringHandling.METHOD_CharLength, MethodType.methodType(int.class), null));
	}

}
