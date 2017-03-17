package de.wwu.muggl.test.symbolic;

import de.wwu.muggl.NotYetSupported;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.test.TestSkeletonSymbolic;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import org.apache.log4j.Level;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.lang.invoke.MethodType;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestReflectiveMethodInvocation extends TestSkeletonSymbolic {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.TRACE);
			Globals.getInst().symbolicExecLogger.setLevel(Level.TRACE);
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

	@Test // if you ever get this test to run, please mail me: max.schulze (aet) posteo.de
	@Category(NotYetSupported.class)
	public final void testApplicationMugglExecuteCountingReflective()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMSymbolicMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.CountWordLength.METHOD_countingreflective,
				MethodType.methodType(int.class, int.class), (Object[]) new Object[] { IntConstant.getInstance(2) });
	}

}
