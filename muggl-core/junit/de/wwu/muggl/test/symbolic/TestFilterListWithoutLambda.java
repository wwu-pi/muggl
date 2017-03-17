package de.wwu.muggl.test.symbolic;

import de.wwu.muggl.NotYetSupported;
import de.wwu.muggl.binaryTestSuite.ListIncGlobalCount;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeletonSymbolic;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.UndefinedValue;
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
public class TestFilterListWithoutLambda extends TestSkeletonSymbolic {
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

	@Test // should at least finish sucessfully
	@Category(NotYetSupported.class)
	public final void testApplicationMugglExecuteSimpleSymbolic()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMSymbolicMethodRunnerHelper.runMethod(classLoader, ListIncGlobalCount.class.getCanonicalName(),
				"executeWithoutLambda", MethodType.methodType(int.class, int.class),
				(Object[]) new Object[] { new UndefinedValue() });
	}

}
