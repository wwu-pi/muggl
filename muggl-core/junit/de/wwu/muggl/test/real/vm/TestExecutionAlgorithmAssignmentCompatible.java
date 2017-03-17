package de.wwu.muggl.test.real.vm;

import de.wwu.muggl.NotYetSupported;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.test.TestSkeleton;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ExecutionAlgorithms;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import org.apache.log4j.Level;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.lang.invoke.MethodType;

import static org.junit.Assert.assertTrue;

/**
 * Test the "Assignment compatibility" logic JVM spec § 4.9.2 and JLS §5.2
 * 
 * @author Max Schulze
 *
 */

// FIXME MXS dieser Test ist eigentlich unvollständig. Die Primitiven, die ich an chekForAss... schicke kommen da ja
// nicht an sondern werden zu ReferenceTypen java.lang.* also testet es nicht ganz das gewollte.
public class TestExecutionAlgorithmAssignmentCompatible extends TestSkeleton {
	private static ExecutionAlgorithms ea;
	private static Application application;

	private static MugglClassLoader classLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!isForbiddenChangingLogLevel) {
			Globals.getInst().changeLogLevel(Level.WARN);
			Globals.getInst().parserLogger.setLevel(Level.ERROR);
		}

		// Need dummy classloader, classFile to get an Application / VM Handle
		classLoader = new MugglClassLoader(new String[] { "./", "./junit-res/" });

		ClassFile classFile = classLoader
				.getClassAsClassFile(de.wwu.muggl.binaryTestSuite.CountWordLength.class.getCanonicalName(), true);

		Method method = classFile.getMethodByNameAndDescriptor(
				de.wwu.muggl.binaryTestSuite.CountWordLength.METHOD_counting,
				MethodType.methodType(long.class, int.class).toMethodDescriptorString());

		application = new Application(classLoader, classFile.getName(), method);
		// application.start();

		ea = new ExecutionAlgorithms(classLoader);

	}

	// If the descriptor type is boolean , byte , char , short , or int , then the value must be an int .

	@Test
	public void runTest01() throws ExecutionException {

		assertTrue("should be legal: int:boolean ",
				ea.checkForAssignmentCompatibility((int) 1, "boolean", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest03() throws ExecutionException {
		assertTrue("should be legal: int:short",
				ea.checkForAssignmentCompatibility((int) 1, "short", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest04() throws ExecutionException {
		assertTrue("should be legal: int:byte",
				ea.checkForAssignmentCompatibility((int) 1, "byte", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest05() throws ExecutionException {
		assertTrue("should be legal: int:char",
				ea.checkForAssignmentCompatibility((int) 1, "char", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest23() throws ExecutionException {
		assertTrue("should be legal: int",
				ea.checkForAssignmentCompatibility((int) 1, "int", application.getVirtualMachine(), false));
	}

	// target is java.lang.*
	@Test
	public void runTest21() throws ExecutionException {
		assertTrue("should be illegal: int:java.lang.Boolean", !ea.checkForAssignmentCompatibility((int) 1,
				"java.lang.Boolean", application.getVirtualMachine(), false));
	}

	@Test
	@Category(NotYetSupported.class)
	public void runTest17() throws ExecutionException {
		assertTrue("should be legal: int:java.lang.Byte",
				ea.checkForAssignmentCompatibility((int) 1, "java.lang.Byte", application.getVirtualMachine(), false));
	}

	@Test
	@Category(NotYetSupported.class)
	public void runTest16() throws ExecutionException {
		assertTrue("should be legal: int:java.lang.Charater", ea.checkForAssignmentCompatibility((int) 1,
				"java.lang.Character", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest22() throws ExecutionException {
		assertTrue("should be illegal: int:java.lang.Short", !ea.checkForAssignmentCompatibility((int) 1,
				"java.lang.Short", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest11() throws ExecutionException {
		assertTrue("should be legal: int:java.lang.Integer", ea.checkForAssignmentCompatibility((int) 1,
				"java.lang.Integer", application.getVirtualMachine(), false));
	}

	// If the descriptor type is float , long , or double , then the value must be a float , long , or double ,
	// respectively.
	@Test
	public void runTest07() throws ExecutionException {
		assertTrue("should be legal: double:double",
				ea.checkForAssignmentCompatibility((double) 1.2, "double", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest08() throws ExecutionException {
		assertTrue("should be legal: float:float",
				ea.checkForAssignmentCompatibility((float) 1.2, "float", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest09() throws ExecutionException {
		assertTrue("should be legal: long:long",
				ea.checkForAssignmentCompatibility((long) 1.2, "long", application.getVirtualMachine(), false));
	}

	// allowed, non-primitive classes
	@Test
	public void runTest18() throws ExecutionException {
		assertTrue("should be legal: double:java.lang.Double", ea.checkForAssignmentCompatibility((double) 1.2,
				"java.lang.Double", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest19() throws ExecutionException {
		assertTrue("should be legal: float:java.lang.Float", ea.checkForAssignmentCompatibility((float) 1.2,
				"java.lang.Float", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest20() throws ExecutionException {
		assertTrue("should be legal: long:java.lang.Long", ea.checkForAssignmentCompatibility((long) 1.2,
				"java.lang.Long", application.getVirtualMachine(), false));
	}

	// mixes should be illegal
	@Test
	public void runTest02() throws ExecutionException {
		assertTrue("should be illegal: java.lang.Boolean:boolean",
				!ea.checkForAssignmentCompatibility((Boolean) true, "boolean", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest13() throws ExecutionException {
		assertTrue("should be legal: boolean:java.lang.Boolean",
				ea.checkForAssignmentCompatibility(true, "java.lang.Boolean", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest15() throws ExecutionException {
		assertTrue("should be illegal: char:char",
				!ea.checkForAssignmentCompatibility((char) 1, "char", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest10() throws ExecutionException {
		assertTrue("should be illegal: long:double",
				!ea.checkForAssignmentCompatibility((long) 1.2, "double", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest14() throws ExecutionException {
		assertTrue("should be illegal: long:float",
				!ea.checkForAssignmentCompatibility((long) 1.2, "float", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest12() throws ExecutionException {
		assertTrue("should be illegal: float:long",
				!ea.checkForAssignmentCompatibility((float) 1.2, "long", application.getVirtualMachine(), false));
	}

	@Test
	public void runTest06() throws ExecutionException {
		assertTrue("should be illegal: float:double",
				!ea.checkForAssignmentCompatibility((float) 1.2, "double", application.getVirtualMachine(), false));
	}

	// cannot assign null to a primitive type
	@Test
	public void runTest24() throws ExecutionException {
		assertTrue("should be illegal: assigning null to primitive type",
				!ea.checkForAssignmentCompatibility(null, "double", application.getVirtualMachine(), false));
	}

	// a value of the null type (the null reference is the only such value) may be
	// assigned to any reference type, resulting in a null reference of that type.
	@Test
	public void runTest25() throws ExecutionException {
		assertTrue("should be legal: assigning null to reference type",
				ea.checkForAssignmentCompatibility(null, "java.lang.Boolean", application.getVirtualMachine(), false));
	}

	@SuppressWarnings("restriction")
	@Test
	public void runTestTransientInterface() throws ExecutionException, ClassFileException {
		ClassFile classFile1 = classLoader
				.getClassAsClassFile(sun.reflect.generics.tree.ClassSignature.class.getCanonicalName(), true);
		Objectref objectref = application.getVirtualMachine().getAnObjectref(classFile1);
		assertTrue("should be legal: A to C, when A implements B (B extends C)", ea.checkForAssignmentCompatibility(
				objectref, "sun.reflect.generics.tree.Tree", application.getVirtualMachine(), false));
	}

}
