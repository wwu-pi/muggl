package de.wwu.muggl.test.real.java7;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class Java7Execution extends TestSkeleton {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.ALL);
			Globals.getInst().parserLogger.setLevel(Level.WARN);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private MugglClassLoader classLoader;

	@Before
	public void setUp() throws Exception {
		classLoader = new MugglClassLoader(mugglClassLoaderPaths);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test // läuft
	public final void testBinaryInLiterals() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(85,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Java7Changes.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.Java7Changes.METHOD_testBinaryInLiterals,
						MethodType.methodType(int.class), (Object[]) null));
	}

	@Test // läuft
	public final void testUnderscoreLiteral() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1234567,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Java7Changes.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.Java7Changes.METHOD_testUnderscoreLiteral,
						MethodType.methodType(int.class), (Object[]) null));
	}

	@Test // fails, hashCodes not in sync
	public final void testStringSwitch() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(1,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.Java7Changes.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.Java7Changes.METHOD_testStringSwitch,
						MethodType.methodType(int.class), (Object[]) null));
	}

}
