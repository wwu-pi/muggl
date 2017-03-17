package de.wwu.muggl.test.symbolic;

import de.wwu.muggl.NotYetSupported;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.solvers.expressions.BooleanConstant;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.test.TestSkeletonSymbolic;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.UndefinedValue;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import org.apache.log4j.Level;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.lang.invoke.MethodType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestObjectManipulation extends TestSkeletonSymbolic {
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

	@Test // currently fails, because upon putfield, it starts again to execute the static initializer... which is
	// wrong.
	public final void testPutstaticNull() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMSymbolicMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.Putfield.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.Putfield.METHOD_testPutStaticNull, MethodType.methodType(boolean.class),
				null));

	}

	@Test
	public final void testPutfieldNull() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMSymbolicMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.PutfieldNoStaticInitializers.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.Putfield.METHOD_testPutfieldNull, MethodType.methodType(boolean.class),
				null));

	}

	@Test
	@Category(NotYetSupported.class)
	public final void testPutStaticBoolean() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		Object res = TestVMSymbolicMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.PutfieldNoStaticInitializers.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.Putfield.METHOD_testPutStaticBoolean,
				MethodType.methodType(boolean.class, boolean.class), new Object[] { new UndefinedValue() });
		assertTrue(res instanceof IntConstant);
	}

	@Test // uncovers flaws in the AssignmentCompatibility check
	@Category(NotYetSupported.class)
	public final void testPutStaticBooleanConst() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		Object res = TestVMSymbolicMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.PutfieldNoStaticInitializers.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.Putfield.METHOD_testPutStaticBoolean,
				MethodType.methodType(boolean.class, boolean.class),
				new Object[] { BooleanConstant.getInstance(false) });
		assertTrue(res instanceof IntConstant);
		assertEquals(1, ((IntConstant) res).getValue());

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void testPutStaticInt() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		Object res = TestVMSymbolicMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.PutfieldNoStaticInitializers.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.Putfield.METHOD_testPutStaticInt,
				MethodType.methodType(boolean.class, int.class), new Object[] { new UndefinedValue() });
		assertTrue(res instanceof IntConstant);

	}

	@Test // läuft
	@Category(NotYetSupported.class)
	public final void testPutStaticIntConstant() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		Object res = TestVMSymbolicMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.PutfieldNoStaticInitializers.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.Putfield.METHOD_testPutStaticInt,
				MethodType.methodType(boolean.class, int.class), new Object[] { IntConstant.FIVE });
		assertTrue(res instanceof IntConstant);
		assertEquals(1, ((IntConstant) res).getIntValue());

	}

}
