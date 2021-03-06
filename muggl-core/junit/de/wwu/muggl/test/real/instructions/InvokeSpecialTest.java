package de.wwu.muggl.test.real.instructions;

import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class InvokeSpecialTest extends TestSkeleton {
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
	// @Test
	public final void testApplicationMugglParseClassFileBootstrapMethod()
			throws ClassFileException, InitializationException {
		ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokespecial.LambdaDemoMinimal.class.getCanonicalName(), true);

		for (Attribute attrib : classFile.getAttributes()) {
			if (attrib instanceof AttributeBootstrapMethods) {
				assertEquals(1, ((AttributeBootstrapMethods) attrib).getNumBootstrapMethods());
				assertEquals(3,
						((AttributeBootstrapMethods) attrib).getBootstrapMethods()[0].getNumBootstrapArguments());

			}
		}
		classFile.getAttributes();

	}

	// @Test
	public final void testApplicationMugglVMRunInvokeSpecial()
			throws ClassFileException, InitializationException, InterruptedException {

		TestVMNormalMethodRunnerHelper.runMethodNoArgVoid(classLoader,
				de.wwu.muggl.binaryTestSuite.invokespecial.LambdaDemoMinimal.class.getCanonicalName(), "executeTest");

	}

}
