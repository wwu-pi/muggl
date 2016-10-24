package de.wwu.muggl.test.real.instructions;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.real.vm.TestVMNormalMethodRunnerHelper;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestSharedSecrets {
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

	// @Test
	public final void testClassObjectrefSuperclass()
			throws ClassFileException, InitializationException, InterruptedException {
		ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(), true);

		Method method = classFile.getMethodByNameAndDescriptor("<clinit>",
				MethodType.methodType(void.class).toMethodDescriptorString());

		Application application = new Application(classLoader, classFile.getName(), method);

		application.start();

		while (!application.getExecutionFinished()) {
			Thread.sleep(Globals.SAFETY_SLEEP_DELAY);
		}

		Objectref objectref;
		objectref = application.getVirtualMachine().getAnObjectref(classFile);

	}

	@Test
	public final void testGetSuperclass() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.lang.Enum",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(),
						"getMySuperClass", MethodType.methodType(String.class), null));

	}

	@Test
	public final void testIterateValues() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(),
						"iterateValues", MethodType.methodType(int.class), null));

	}
	@Test
	public final void testgetSuperSuperclass()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("java.lang.Object",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(),
						"getMySuperSuperClass", MethodType.methodType(String.class), null));

	}

	@Test
	public final void testgetObjectSuperclass()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("npe as expected",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(),
						"getObjectSuperclass", MethodType.methodType(String.class), null));

	}

	// nota: this involves sun.reflect.NativeMethodAccessorImpl.invoke0
	@Test
	public final void testGetValues() throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(2,
				(int) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(), "getValues",
						MethodType.methodType(int.class), null));

	}

	@Test
	public final void testClassIsEnum() throws ClassFileException, InitializationException, InterruptedException {
		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(), "getClassIsEnum",
				MethodType.methodType(boolean.class), null));

	}

	// @Test
	public final void testGetUnsafe() throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokestatic.MySharedSecrets.class.getCanonicalName(), "unsafer");

	}

}
