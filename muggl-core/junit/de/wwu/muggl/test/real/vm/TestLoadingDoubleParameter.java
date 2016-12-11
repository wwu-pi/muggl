package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.binaryTestSuite.doubleParameter.FunctionWithDoubleParameter;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestLoadingDoubleParameter extends TestSkeleton {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.ALL);
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
	public final void testApplicationMugglVMRealExecDoubleParameter()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("result: 1.23",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						FunctionWithDoubleParameter.class.getCanonicalName(),
						FunctionWithDoubleParameter.METHOD_makeStringWithDoubleParameters,
						MethodType.methodType(String.class, double.class), (Object[]) new Double[] { 1.23 }));

	}

	@Test // läuft
	public final void testApplicationMugglVMRealExecCalcDoubleParameter()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1.0, (double) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				FunctionWithDoubleParameter.class.getCanonicalName(),
				FunctionWithDoubleParameter.METHOD_calcWithDoubleParameters,
				MethodType.methodType(double.class, double.class, double.class), (Object[]) new Double[] { 2.1, 1.1 }),
				0.0000001);

	}

}
