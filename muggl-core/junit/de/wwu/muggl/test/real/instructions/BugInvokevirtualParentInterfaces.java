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
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ResolutionAlgorithms;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class BugInvokevirtualParentInterfaces extends TestSkeleton {
	MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.ALL);
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
	public final void testApplicationMugglVMRunBugInvokevirtualParentInterface()
			throws ClassFileException, InitializationException, InterruptedException {
		assertEquals("2",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName(), "forTesting",
						MethodType.methodType(String.class, Integer.class),(Object[]) new Integer[] { 2 }));

	}

	/**
	 * Should find the class in the parent interface and NOT throw an NoSuchMethodError
	 * 
	 * This is the same problem as the default method stream() ( in an parent interface )
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws NoSuchMethodError
	 */
	@Test
	public final void testApplicationMugglVMRunMethodResolutionParentInterfaces()
			throws ClassFileException, InitializationException, NoSuchMethodError {

		final ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName() + "$MySecondType", true);

		ResolutionAlgorithms resolAlg = new ResolutionAlgorithms(classLoader);

		final String[] nameAndType = new String[] { "defaultInMyInterface", "()Ljava/lang/String;" };

		Method method = resolAlg.resolveMethod(classFile, nameAndType);
		assertEquals(false, method.isAccAbstract());
	}

}
