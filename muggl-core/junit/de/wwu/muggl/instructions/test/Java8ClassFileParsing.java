/**
 * 
 */
package de.wwu.muggl.instructions.test;

import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.loading.MugglClassLoader;


/**
 * @author Max Schulze
 *
 */
public class Java8ClassFileParsing {
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Globals.getInst().changeLogLevel(Level.ALL);
	}
	/**
	 * Test method for {@link de.wwu.muggl.vm.loading.MugglClassLoader#MugglClassLoader(java.lang.String[])}.
	 * Only tests correct parsing of binary .class files
	 * This test fails if you class File uses features Muggl cannot understand, i.e. new java bytecode instructions
	 * ( via Exceptions )
	 * @throws ClassFileException 
	 */
	@Test
	public final void testMugglClassLoaderWithJava8Binary() throws ClassFileException {
		  MugglClassLoader classLoader = new MugglClassLoader(new String[]{"./"});
		  ClassFile classFile = classLoader.getClassAsClassFile("junit-res/binary/openjdk/one/eight/zero/ninetyone/CountWordLength.class", true);

		  // small side-test
		  assertEquals(52, classFile.getMajorVersion());
	}


}
