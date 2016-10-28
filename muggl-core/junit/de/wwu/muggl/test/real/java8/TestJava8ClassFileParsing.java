/**
 * 
 */
package de.wwu.muggl.test.real.java8;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * @author Max Schulze
 *
 */
public class TestJava8ClassFileParsing extends TestSkeleton {
	private MugglClassLoader classLoader;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.ALL);
		}
		classLoader = new MugglClassLoader(mugglClassLoaderPaths);
	}

	/**
	 * Test method for {@link de.wwu.muggl.vm.loading.MugglClassLoader#MugglClassLoader(java.lang.String[])}. Only tests
	 * correct parsing of binary .class files This test fails if you class File uses features Muggl cannot understand,
	 * i.e. new java bytecode instructions ( via Exceptions )
	 * 
	 * @throws ClassFileException
	 */
	@Test
	public final void testMugglClassLoaderWithJava8Binary() throws ClassFileException {
		ClassFile classFile = classLoader
				.getClassAsClassFile(de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(), true);

		// small side-test
		assertEquals(52, classFile.getMajorVersion());
	}

	@Test
	public final void testMugglClassLoaderMethodAnnotations() throws ClassFileException {
		ClassFile classFile = classLoader.getClassAsClassFile("sun.reflect.Reflection", true);

		Method m = classFile.getMethodByNameAndDescriptor("getCallerClass",
				MethodType.methodType(Class.class).toMethodDescriptorString());
		assertTrue(m.isCallerSensitive());
	}

}
