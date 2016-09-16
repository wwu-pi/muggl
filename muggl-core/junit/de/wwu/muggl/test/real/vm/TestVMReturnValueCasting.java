package de.wwu.muggl.test.real.vm;

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

public class TestVMReturnValueCasting {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.TRACE);
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
	public final void testGetVMReturnInteger()
			throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertEquals((Integer) 35,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.checkcast.ObjectRefCasting.class.getCanonicalName(),
						"returnJavaLangInteger", "()Ljava/lang/Integer;", null));
	}

	@Test
	public final void testGetVMReturnIntegerCasted()
			throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertEquals((Integer) 34,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.checkcast.ObjectRefCasting.class.getCanonicalName(),
						"returnJavaLangIntegerCasted", "()Ljava/lang/Integer;", null));
	}

	@Test
	public final void testGetVMReturnint() throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertEquals((Integer) 33,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.checkcast.ObjectRefCasting.class.getCanonicalName(), "returnInt",
						"()I", null));
	}

	@Test
	public final void testGetVMReturnintCasted()
			throws ClassFileException, InitializationException, InterruptedException {
		org.junit.Assert.assertEquals((Integer) 32,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.checkcast.ObjectRefCasting.class.getCanonicalName(),
						"returnIntCasted", "()I", null));
	}

}
