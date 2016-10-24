package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import de.wwu.muggl.binaryTestSuite.HashMapTest;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestHashMap {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.TRACE);
		Globals.getInst().parserLogger.setLevel(Level.ERROR);
	}
	
	@Rule
	public Timeout globalTimeout = Timeout.seconds(15); // 10 seconds max per method tested

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

	// @Test // läuft
	public final void test_HashMap() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("Freixenet",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader, HashMapTest.class.getCanonicalName(),
						HashMapTest.METHOD_test_HashMap, MethodType.methodType(String.class), null));

	}

	// @Test // läuft
	public final void test_HashMapComplicatedClass()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("fieldFilterMap",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader, HashMapTest.class.getCanonicalName(),
						HashMapTest.METHOD_test_HashMapComplicated, MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_HashMapInHashMap() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("fieldFilterMap1",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader, HashMapTest.class.getCanonicalName(),
						HashMapTest.METHOD_test_HashMapInHashMap, MethodType.methodType(String.class), null));

	}

}
