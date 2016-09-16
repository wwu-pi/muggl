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

/**
 * 
 * Initialization of the IntegerCache calls sun.misc.VM.getSavedProperty(String key) which in the original rt.jar does a
 * check if systems' properties are empty (which they are in muggl at this time of writing). It will then emit a
 * IllegalStateException("Should be non-empty if initialized").
 * 
 * To cirumvent this as a dirty fix, there is a patched VM.java in muggl-core/sun/misc that does not do this check and
 * hence these checks here should pass.
 * 
 * @author Max Schulze
 *
 */
public class BugIntegerCacheMonitor {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Globals.getInst().changeLogLevel(Level.TRACE);
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
	public final void testIntegerEquality() throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.IntegerCache.class.getCanonicalName(), "execute");

	}

}
