package de.wwu.muggl.test.real.vm;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ResolutionAlgorithms;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class TestMethodResolution extends TestSkeleton {
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
	 * Find a default method in the Superinterface of the superclass
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws NoSuchMethodError
	 */
	// @Test
	public final void testFindMethodSuperinterfaceOfSuperclass()
			throws ClassFileException, InitializationException, NoSuchMethodError {

		final ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName() + "$MySecondType", true);

		ResolutionAlgorithms resolAlg = new ResolutionAlgorithms(classLoader);

		final String[] nameAndType = new String[] { "defaultInMyInterface", "()Ljava/lang/String;" };

		resolAlg.resolveMethod(classFile, nameAndType);

	}

	/**
	 * Find a default method in a (direct) superinterface
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws NoSuchMethodError
	 */
	@Test
	public final void testFindMethodSuperinterface()
			throws ClassFileException, InitializationException, NoSuchMethodError {

		final ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName() + "$MySecondType", true);

		ResolutionAlgorithms resolAlg = new ResolutionAlgorithms(classLoader);

		final String[] nameAndType = new String[] { "defaultInMySecondInterface", "()Ljava/lang/String;" };

		resolAlg.resolveMethod(classFile, nameAndType);

	}

	/**
	 * Find a method in a (direct) superclass
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws NoSuchMethodError
	 */
	@Test
	public final void testFindMethodDirectSuperclass()
			throws ClassFileException, InitializationException, NoSuchMethodError {

		final ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName() + "$MySecondType", true);

		ResolutionAlgorithms resolAlg = new ResolutionAlgorithms(classLoader);

		final String[] nameAndType = new String[] { "doNothingImportant", "()V" };

		resolAlg.resolveMethod(classFile, nameAndType);

	}

	/**
	 * Find a method in a (indirect) superclass (java.lang.Object)
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws NoSuchMethodError
	 */
	@Test
	public final void testFindMethodIndirectSuperclass()
			throws ClassFileException, InitializationException, NoSuchMethodError {

		final ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName() + "$MySecondType", true);

		ResolutionAlgorithms resolAlg = new ResolutionAlgorithms(classLoader);

		final String[] nameAndType = new String[] { "toString", "()Ljava/lang/String;" };

		resolAlg.resolveMethod(classFile, nameAndType);

	}

	@Test
	public final void testFindMethodSuperClassOfSuperInterface()
			throws ClassFileException, InitializationException, NoSuchMethodError {

		final ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.invokevirtual.MyType.class.getCanonicalName() + "$MySecondType", true);

		ResolutionAlgorithms resolAlg = new ResolutionAlgorithms(classLoader);

		final String[] nameAndType = new String[] { "defaultInMyThirdInterface", "()Ljava/lang/String;" };

		resolAlg.resolveMethod(classFile, nameAndType);

	}

	@Test
	public final void testFindMethodGenericReturnType()
			throws ClassFileException, InitializationException, NoSuchMethodError {

		final ClassFile classFile = classLoader
				.getClassAsClassFile(sun.reflect.generics.repository.ClassRepository.class.getCanonicalName(), true);

		ResolutionAlgorithms resolAlg = new ResolutionAlgorithms(classLoader);

		final String[] nameAndType = new String[] { "parse",
				MethodType.methodType(sun.reflect.generics.repository.ClassRepository.class, String.class)
						.toMethodDescriptorString() };

		resolAlg.resolveMethod(classFile, nameAndType);

	}

}
