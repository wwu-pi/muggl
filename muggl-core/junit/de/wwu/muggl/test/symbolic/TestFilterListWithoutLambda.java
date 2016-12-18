package de.wwu.muggl.test.symbolic;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.binaryTestSuite.ListIncGlobalCount;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeletonSymbolic;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.UndefinedValue;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

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
	public final void testApplicationMugglExecuteSimpleSymbolic()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethod(classLoader, ListIncGlobalCount.class.getCanonicalName(),
				"executeWithoutLambda", MethodType.methodType(int.class, int.class),
				(Object[]) new Object[] { new UndefinedValue() });
	}

}
