package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import de.wwu.muggl.binaryTestSuite.HashMapTest;
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
public class TestHashMap extends TestSkeleton {
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
	public final void test_HashMap() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("Freixenet",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader, HashMapTest.class.getCanonicalName(),
						HashMapTest.METHOD_test_HashMap, MethodType.methodType(String.class), null));
	}

	@Test // this test causes the System.exit() to thwrow a NullPointerException. Don't know why
	@Ignore
	public final void test_HashMapConcurrent()
			throws ClassFileException, InitializationException, InterruptedException {

		assertEquals("Freixenet",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader, HashMapTest.class.getCanonicalName(),
						HashMapTest.METHOD_test_HashMapConcurrent, MethodType.methodType(String.class), null));
	}

	@Test
	public final void test_HashMapComplicatedClass()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("fieldFilterMap",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader, HashMapTest.class.getCanonicalName(),
						HashMapTest.METHOD_test_HashMapComplicated, MethodType.methodType(String.class), null));

	}

	@Test
	public final void test_HashMapInHashMap() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("1 1",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader, HashMapTest.class.getCanonicalName(),
						HashMapTest.METHOD_test_HashMapInHashMap, MethodType.methodType(String.class), null));

	}

	/**
	 * Will look if a newly initialized HashTable is empty. The function .isEmpty is synchronized so this test will fail
	 * if there are problems with monitor support
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws InterruptedException
	 */
	@Test
	public final void testApplicationMugglVMRunHashTableEmpty()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokevirtual.HashTableEmpty.class.getCanonicalName(), "execute");
	}

}
