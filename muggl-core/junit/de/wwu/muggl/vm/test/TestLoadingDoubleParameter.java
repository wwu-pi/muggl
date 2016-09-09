package de.wwu.muggl.vm.test;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestLoadingDoubleParameter {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.ALL);
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
	public final void testApplicationMugglVMSymbolicExecDoubleParameter()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				"binary.openjdk.one.eight.zero.ninetyone.doubleParameter.FunctionWithDoubleParameter",
				"makeStringWithDoubleParameters", "(DD)Ljava/lang/String;", (Object[]) new Double[] { 1.23, 2.34 });

	}

}
