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
public class TestEnumWitValuesMethodCloneCall {
	MugglClassLoader classLoader;

	// @Rule
	// public Timeout globalTimeout = Timeout.seconds(10); // 10 seconds max per method tested

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.TRACE);
		Globals.getInst().parserLogger.setLevel(Level.WARN);
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
	public final void testIterateValues() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.EnumWitValuesMethodCloneCall.class.getCanonicalName(),
						"iterateValues", MethodType.methodType(int.class), null));

	}

}
