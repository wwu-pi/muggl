package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.bytecode.Invokevirtual;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class TestVMMugglJavaWrapper {
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


	/**
	 * Look at MugglTojava:851 why this class comparison fails
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws InterruptedException
	 * @throws InvalidInstructionInitialisationException
	 * @throws ConversionException
	 */
	@Test
	public final void testVMWrapUnwrapObjects() throws ClassFileException, InitializationException, InterruptedException, InvalidInstructionInitialisationException, ConversionException {
		
		// need dummy (class and method) to create application
		ClassFile classFile = classLoader.getClassAsClassFile(de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(), true);

		Method method = classFile.getMethodByNameAndDescriptor("isbooted", "()Z");
	
		Application application = new Application(classLoader, classFile.getName(), method);
		MugglToJavaConversion conversion = new MugglToJavaConversion(application.getVirtualMachine());
		
		// need a class that has an ancestor different than java.lang.Class
		Class<?> clazz = (new Invokevirtual(null)).getClass().getSuperclass();
		
		Object mugglObj = conversion.toMuggl(clazz, false);
		
		Object backConverted = conversion.toJava(mugglObj);
		assertEquals(clazz, (Class<?>) backConverted);
	}

}
