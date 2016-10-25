package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * Test Initialization of static/object class from the VM Management world.
 * 
 * @author Max Schulze
 *
 */
public class TestClassInitialization extends TestSkeleton {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.WARN);
		Globals.getInst().parserLogger.setLevel(Level.WARN);
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

	/**
	 * @jvms § 5.5
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws InterruptedException
	 */
	@Test
	public final void testStaticInitialization()
			throws ClassFileException, InitializationException, InterruptedException {
		ClassFile classFile = classLoader
				.getClassAsClassFile(de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(), true);

		Method method = classFile.getMethodByNameAndDescriptor(
				de.wwu.muggl.binaryTestSuite.CountWordLength.METHOD_counting,
				MethodType.methodType(long.class, int.class).toMethodDescriptorString());

		Application application = new Application(classLoader, classFile.getName(), method);
		InitializedClass initCl = classFile.getTheInitializedClass(application.getVirtualMachine(), true);

		assertEquals("counting", initCl.getField(classFile.getFieldByName("METHOD_counting")));

		application.finalizeApplication();
	}

	@Test
	public final void testStaticInitializationFriend()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals(true,
				(boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.CountWordLength.METHOD_returnStaticFieldFriendClass,
						MethodType.methodType(boolean.class), (Object[]) new Object[] {}));
	}

	@Test
	public final void testInstanceInitialization() throws ClassFileException, InitializationException,
			InterruptedException, ExecutionException, InvalidInstructionInitialisationException, ConversionException {
		ClassFile classFile = classLoader
				.getClassAsClassFile(de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(), true);

		Method method = classFile.getMethodByNameAndDescriptor(
				de.wwu.muggl.binaryTestSuite.CountWordLength.METHOD_counting,
				MethodType.methodType(long.class, int.class).toMethodDescriptorString());

		Application application = new Application(classLoader, classFile.getName(), method);
		@SuppressWarnings("unused")
		InitializedClass initCl = classFile.getTheInitializedClass(application.getVirtualMachine(), true);
		
		Objectref objectref = application.getVirtualMachine().getAndInitializeObjectref(classFile.getInitializedClass());
				
		assertEquals("asdfghjkl", new MugglToJavaConversion(application.getVirtualMachine())
				.toJava(objectref.getField(classFile.getFieldByName("teststring"))));

		application.finalizeApplication();
	}

	@Test
	public final void testApplicationExecConstantValue()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("counting",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(), "returnConstantValue",
						MethodType.methodType(String.class), (Object[]) new Object[] {}));
	}

}
