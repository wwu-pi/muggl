package de.wwu.muggl.test.real.java8;

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
public class TestLambdaHandModified extends TestSkeleton {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.TRACE);
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

//	@Test
	public final void testLambdaHandInvokedynamic()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2, TestVMNormalMethodRunnerHelper.runMethod(classLoader, "PrintNumberListStaticArg", "execute",
				MethodType.methodType(int.class, int.class), (Object[]) new Object[] { 4 }));
	}

	@Test
	public final void testLambdaHandBootstrapCall()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2, TestVMNormalMethodRunnerHelper.runMethod(classLoader, "PrintNumberListStaticArg",
				"bootstrapTester", MethodType.methodType(void.class), (Object[]) new Object[] {}));
	}

}
