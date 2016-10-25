package de.wwu.muggl.test.real.vm;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class TestVMReturnValueCasting extends TestSkeleton {
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
	public final void testGetVMReturnInteger()
			throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertEquals((Integer) 35,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.checkcast.ObjectRefCasting.class.getCanonicalName(),
						"returnJavaLangInteger", MethodType.methodType(Integer.class), null));
	}

	@Test
	public final void testGetVMReturnIntegerCasted()
			throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertEquals((Integer) 34,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.checkcast.ObjectRefCasting.class.getCanonicalName(),
						"returnJavaLangIntegerCasted", MethodType.methodType(Integer.class), null));
	}

	@Test
	public final void testGetVMReturnint() throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertEquals((Integer) 33,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.checkcast.ObjectRefCasting.class.getCanonicalName(), "returnInt",
						MethodType.methodType(int.class), null));
	}

	@Test
	public final void testGetVMReturnintCasted()
			throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertEquals((Integer) 32,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.checkcast.ObjectRefCasting.class.getCanonicalName(),
						"returnIntCasted", MethodType.methodType(int.class), null));
	}

}
