package de.wwu.muggl.test.real.vm;

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
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class TestMugglCatchCorrectException {
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
	public final void testArrayLength() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((char) ':',
				(char) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.CatchCorrectExceptions.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.CatchCorrectExceptions.METHOD_ArrayOutOfBound,
						MethodType.methodType(char.class), null));

	}

}
