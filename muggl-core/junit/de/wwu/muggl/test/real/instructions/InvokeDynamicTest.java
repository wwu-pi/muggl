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
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Attribute;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeBootstrapMethods;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class InvokeDynamicTest extends TestSkeleton {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.TRACE);
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

	/**
	 * Test correct parsing of BootstrapMethods Attribute
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 */
	@Test
	public final void testApplicationMugglParseClassFileBootstrapMethod()
			throws ClassFileException, InitializationException {
		ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemo.class.getCanonicalName(), true);

		for (Attribute attrib : classFile.getAttributes()) {
			if (attrib instanceof AttributeBootstrapMethods) {
				assertEquals(2, ((AttributeBootstrapMethods) attrib).getNumBootstrapMethods());
				assertEquals(3,
						((AttributeBootstrapMethods) attrib).getBootstrapMethods()[0].getNumBootstrapArguments());

			}
		}
		classFile.getAttributes();

	}

	// @Test
	public final void testlambdaSugarRunnable()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemo.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemo.METHOD_lambdaSugarRunnable,
				MethodType.methodType(void.class), null);
	}

	@Test
	public final void testGenerateLambdaMetafactoryManual()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemo.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemo.METHOD_GenerateLambdaMetafactoryManual,
				MethodType.methodType(void.class), null);
	}

	@Test
	public final void testlambdaMetafactoryAuto()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemo.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemo.METHOD_lambdaMetafactoryAuto,
				MethodType.methodType(void.class), null);
	}

}
