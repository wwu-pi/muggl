package de.wwu.muggl.vm.test;

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
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
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
public class BugInvokevirtualParentInterfaces {
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
		classLoader = new MugglClassLoader(
				new String[] { "./", "./junit-res/" });
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testApplicationMugglVMRunBugInvokevirtualParentInterface()
			throws ClassFileException, InitializationException {

		ClassFile classFile = classLoader.getClassAsClassFile(
				"binary.openjdk.one.eight.zero.ninetyone.buginvokevirtual.MyType",
				true);

		Method method = classFile.getMethodByNameAndDescriptor("forTesting",
				"(Ljava/lang/Integer;)Ljava/lang/String;");

		method.setPredefinedParameters((Object[]) new Integer[] { 2 });

		Application application = new Application(classLoader,
				classFile.getName(), method);
		application.start();

		synchronized (application) {
			try {
				// System.out.println("Waiting for b to complete...");
				application.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Find out if execution finished successfully.
		if (application.errorOccured()) {
			// There was an error.
			fail("Execution did not finish successfully. The reason is:\n"
					+ application.fetchError());
		} else {
			if (application.getHasAReturnValue()) {
				Object object = application.getReturnedObject();
				if (object == null) {
				} else {
				}
			} else if (application.getThrewAnUncaughtException()) {
				Objectref objectref = (Objectref) application
						.getReturnedObject();

				ClassFile throwableClassFile = objectref.getInitializedClass()
						.getClassFile();
				Field detailMessageField = throwableClassFile
						.getFieldByName("detailMessage", true);
				ClassFile stringClassFile = application.getVirtualMachine()
						.getClassLoader()
						.getClassAsClassFile("java.lang.String");
				Field stringValueField = stringClassFile
						.getFieldByNameAndDescriptor("value", "[C");
				Objectref stringObjectref = (Objectref) objectref
						.getField(detailMessageField);
				String message;
				if (stringObjectref == null) {
					message = "null";
				} else {
					Arrayref arrayref = (Arrayref) stringObjectref
							.getField(stringValueField);

					// Convert it.
					char[] characters = new char[arrayref.length];
					for (int a = 0; a < arrayref.length; a++) {
						characters[a] = (Character) arrayref.getElement(a);
					}
					message = new String(characters);
				}

				fail("Uncaught exception, no suitable exception handler: "
						+ throwableClassFile.getName().replace("/", ".") + " ("
						+ message + ")");

			} else {
			}
		}
	}

	/**
	 * Should find the class in the parent interface and NOT throw an
	 * NoSuchMethodError
	 * 
	 * This is the same problem as the default method stream() ( in an parent interface )
	 * 
	 * @throws ClassFileException
	 * @throws InitializationException
	 * @throws NoSuchMethodError
	 */
	//@Test
	public final void testApplicationMugglVMRunMethodResolutionParentInterfaces()
			throws ClassFileException, InitializationException,
			NoSuchMethodError {

		final ClassFile classFile = classLoader.getClassAsClassFile(
				"binary.openjdk.one.eight.zero.ninetyone.buginvokevirtual.MyType$MySecondType",
				true);

		ResolutionAlgorithms resolAlg = new ResolutionAlgorithms(classLoader);

		final String[] nameAndType = new String[] { "defaultInMyInterface",
				"()Ljava/lang/String;" };

		Method method = resolAlg.resolveMethod(classFile, nameAndType);
		assertEquals(false, method.isAccAbstract());
	}

}
