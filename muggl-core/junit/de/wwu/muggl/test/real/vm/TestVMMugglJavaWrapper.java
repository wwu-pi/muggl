package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.stream.IntStream;

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
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.PrimitiveWrappingImpossibleException;
import de.wwu.muggl.vm.initialization.ReferenceValue;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * Testing the MugglWrapper toMuggl and toJava wrapper and expecting same object back.
 * 
 * @author Max Schulze
 *
 */
public class TestVMMugglJavaWrapper {
	private static MugglClassLoader classLoader;
	private static MugglToJavaConversion conversion;
	private static Application application;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Globals.getInst().changeLogLevel(Level.TRACE);
		Globals.getInst().parserLogger.setLevel(Level.ERROR);

		classLoader = new MugglClassLoader(new String[] { "./", "./junit-res/" });
		// need dummy (class and method) to create application
		ClassFile classFile = classLoader.getClassAsClassFile(
				de.wwu.muggl.binaryTestSuite.testVMInit.TestInitializeSystemClass.class.getCanonicalName(), true);

		Method method = classFile.getMethodByNameAndDescriptor("isbooted", "()Z");

		application = new Application(classLoader, classFile.getName(), method);

		conversion = new MugglToJavaConversion(application.getVirtualMachine());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Look at MugglTojava:851 why this class comparison fails
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws InterruptedException
	 * @throws InvalidInstructionInitialisationException
	 * @throws ConversionException
	 */
	// @Test
	public final void testVMWrapUnwrapObjects() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		// need a class that has an ancestor different than java.lang.Class
		Class<?> clazz = (new Invokevirtual(null)).getClass().getSuperclass();

		Object mugglObj = conversion.toMuggl(clazz, false);

		Object backConverted = conversion.toJava(mugglObj);
		assertEquals(clazz, (Class<?>) backConverted);
	}

	// @Test
	public final void testArraytoMugglArrayRef() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		byte[] testing = new byte[] { 2, 3, 4 };
		Object mugglObj = conversion.toMuggl(testing, false);

		Byte[] javaObj = (Byte[]) conversion.toJava(mugglObj);
		byte[] byteArray = new byte[javaObj.length];

		IntStream.range(0, javaObj.length).forEach(i -> byteArray[i] = javaObj[i]);

		assertArrayEquals(testing, byteArray);

	}

	// @Test
	public final void testtoMugglByte() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		byte testing = 2;

		Object mugglObj = conversion.toMuggl(testing, true);

		Object javaObj = conversion.toJava(mugglObj);

		assertEquals(testing, javaObj);

	}

	@Test
	public final void testtoMugglString() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		String testing = "alles wird gut";
		Object mugglObj = conversion.toMuggl(testing, false);
		Object mugglStrin2 = application.getVirtualMachine().getStringCache().getStringObjectref(testing);
		Object javaObj = conversion.toJava(mugglObj);

		assertEquals(testing, javaObj);
		assertEquals(testing, conversion.toJava(mugglStrin2));

	}

	// @Test
	public final void testtoMugglBoolean() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		boolean testing = false;
		Object mugglObj = conversion.toMuggl(testing, true);

		Object javaObj = conversion.toJava(mugglObj);

		assertEquals(testing, javaObj);

	}

	// @Test
	public final void testtoMugglInt() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		Integer testing = 31123;
		Object mugglObj = conversion.toMuggl(testing, false);

		Object javaObj = conversion.toJava(mugglObj);

		assertEquals(testing, javaObj);

	}

	// @Test
	public final void testMugglArrayToJava() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException, NoSuchFieldException, SecurityException,
			PrimitiveWrappingImpossibleException {

		int[][] array = { { 1 }, { 2 } };
		ReferenceValue referenceValue = classLoader.getClassAsClassFile("java.lang.Boolean")
				.getAPrimitiveWrapperObjectref(application.getVirtualMachine());

		Arrayref arrayref = new Arrayref(referenceValue, 1);

		Object mugglObj = conversion.toMuggl(array, true);

		// Array.set(array, a, toJava(arrayref.getElement(a)));
		Object javaObj = conversion.toJava(mugglObj);

		assertArrayEquals(array[0], ((int[][]) javaObj)[0]);
		assertArrayEquals(array[1], ((int[][]) javaObj)[1]);

	}

}
