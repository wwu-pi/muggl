package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.*;

import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class TestMugglArrayrefToObjectref {
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

	// @Test
	public final void testVMArrayref() throws ClassFileException, InitializationException, InterruptedException,
			InvalidInstructionInitialisationException, ConversionException {

		assertEquals((Integer) 5,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testObjectref,
						MethodType.methodType(int.class).toMethodDescriptorString(), null));

	}

	@Test
	public final void testVMArrayrefInvokevirtual() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((String) "1,2,3,4,5",
				(String) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testObjectref2,
						MethodType.methodType(String.class).toMethodDescriptorString(), null));

	}

	// @Test
	public final void testVMIntegerPutByteArray() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((Integer) 6,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testIntegerPutByteArray,
						MethodType.methodType(int.class).toMethodDescriptorString(), null));

	}

	// @Test
	public final void testVMIntegerPutByteArray2() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertEquals((Integer) 6,
				(Integer) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
						de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_testIntegerPutByteArray2,
						MethodType.methodType(int.class).toMethodDescriptorString(), null));

	}

	// @Test
	public final void testVMArrayRefObjectrefBoolean() throws ClassFileException, InitializationException,
			InterruptedException, InvalidInstructionInitialisationException, ConversionException {

		assertTrue((boolean) TestVMNormalMethodRunnerHelper.runMethod(classLoader,
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.class.getCanonicalName(),
				de.wwu.muggl.binaryTestSuite.ArrayrefToObjectref.METHOD_ArrayRefObjectrefBoolean,
				MethodType.methodType(boolean.class).toMethodDescriptorString(), null));

	}

	private class MyTest {
		@SuppressWarnings("unused")
		private byte[] test = new byte[] { 2, 3, 4 };

		public MyTest() {

		}
	}

	// FIXME mxs: kann weg?
	// @Test
	public void ArrayRefObjectrefPut() throws IllegalArgumentException, IllegalAccessException {
		Object testing = (new TestMugglArrayrefToObjectref()).new MyTest();

		Set<java.lang.reflect.Field> allFields = new HashSet<java.lang.reflect.Field>();

		// Process the object's class and any of its super classes.
		boolean firstPass = true;
		Class<?> objectClass = testing.getClass();
		while (objectClass != null) {
			// Get the fields.
			java.lang.reflect.Field[] fields = objectClass.getDeclaredFields();

			// Add the fields.
			for (java.lang.reflect.Field field : fields) {
				/*
				 * Add the fields to the list of found fields if this is either the first pass or if the field found is
				 * not private. By using a HashSet, duplicate entries (an overridden field will be found both in the
				 * class as in one of its super classes) will simply not be added.
				 */
				if (firstPass || !java.lang.reflect.Modifier.isPrivate(field.getModifiers()))
					allFields.add(field);
			}

			// Get the super class.
			objectClass = objectClass.getSuperclass();
			firstPass = false;
		}

		java.lang.reflect.Field javaField = allFields.toArray(new java.lang.reflect.Field[allFields.size()])[0];
		// Ensure accessibility.
		javaField.setAccessible(true);
		Object rawValues = javaField.get(testing);
		assertEquals("byte[]", rawValues.getClass().getSimpleName());
		java.lang.reflect.Field[] tada = rawValues.getClass().getFields();
		Object dddd = Array.get(rawValues, 0);

	}

}
