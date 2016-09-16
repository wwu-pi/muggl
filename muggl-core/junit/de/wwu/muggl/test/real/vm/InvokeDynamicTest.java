package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Attribute;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeBootstrapMethods;
import de.wwu.muggl.vm.execution.ResolutionAlgorithms;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class InvokeDynamicTest {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.TRACE);
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
	 * Test correct parsing of BootstrapMethods Attribute
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 */
	@Test
	public final void testApplicationMugglParseClassFileBootstrapMethod()
			throws ClassFileException, InitializationException {
		ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemoMinimal.class.getCanonicalName(), true);

		for (Attribute attrib : classFile.getAttributes()) {
			if (attrib instanceof AttributeBootstrapMethods) {
				assertEquals(1, ((AttributeBootstrapMethods) attrib).getNumBootstrapMethods());
				assertEquals(3,
						((AttributeBootstrapMethods) attrib).getBootstrapMethods()[0].getNumBootstrapArguments());

			}
		}
		classFile.getAttributes();

	}

	@Test
	public final void testApplicationMugglVMRunInvokeDynamic()
			throws ClassFileException, InitializationException, InterruptedException {
		TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.invokedynamic.LambdaDemoMinimal.class.getCanonicalName(), "main",
				"([Ljava/lang/String;)V", (Object[]) null);
	}

}
