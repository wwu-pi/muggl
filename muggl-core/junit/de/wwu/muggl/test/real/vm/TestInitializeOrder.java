package de.wwu.muggl.test.real.vm;

import de.wwu.muggl.NotYetSupported;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import org.apache.log4j.Level;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.lang.invoke.MethodType;

import static org.junit.Assert.assertTrue;

/**
 * Test when the class and object initializers are called.
 * 
 * @author Max Schulze
 *
 */
public class TestInitializeOrder extends TestSkeleton {
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
	@Category(NotYetSupported.class)
	public final void testInitializations() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.InitializerOrder.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.InitializerOrder.METHOD_testInitializations,
				MethodType.methodType(boolean.class), null));

	}

	@Test
	@Category(NotYetSupported.class)
	public final void testInitializationsArray() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.InitializerOrder.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.InitializerOrder.METHOD_testInitializationsArray,
				MethodType.methodType(boolean.class), null));

	}

	@Test
	@Category(NotYetSupported.class)
	public final void testInitializationsArrayNull() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.InitializerOrder.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.InitializerOrder.METHOD_testInitializationsArrayNull,
				MethodType.methodType(boolean.class), null));

	}

}
