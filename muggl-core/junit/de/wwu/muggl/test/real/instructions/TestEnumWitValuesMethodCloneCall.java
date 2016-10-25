package de.wwu.muggl.test.real.instructions;

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
 * Tests that calls on arrays, e.g. clone() get routed to the java.util.Arrays classFile and not to their component type
 * 
 * @author Max Schulze
 *
 */
public class TestEnumWitValuesMethodCloneCall extends TestSkeleton {
	MugglClassLoader classLoader;

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

	@Before
	public void setUp() throws Exception {
		classLoader = new MugglClassLoader(mugglClassLoaderPaths);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testIterateValues() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.EnumWitValuesMethodCloneCall.class.getCanonicalName(),
						"iterateValues", MethodType.methodType(int.class), null));

	}

}
