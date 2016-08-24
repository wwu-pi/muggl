package de.wwu.muggl.vm.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * 
 * @author Max Schulze
 *
 */
public class Java8Execution {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

	@Test
	public final void testApplicationMugglClassLoaderStringMethod()
			throws ClassFileException, InitializationException {
		MugglClassLoader classLoader = new MugglClassLoader(
				new String[] { "./" });
		ClassFile classFile = classLoader.getClassAsClassFile(
				"junit-res/binary/openjdk/one/eight/zero/ninetyone/CountWordLength.class",
				true);

		Method method = classFile.getMethodByNameAndDescriptor("counting",
				"(I)J");

		method.setPredefinedParameters((Object[]) new Integer[] { 2 });

		Application application = new Application(classLoader,
				classFile.getName(), method);
		application.start();

        synchronized(application){
            try{
                //System.out.println("Waiting for b to complete...");
                application.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

		// Find out if execution finished successfully.
		if (application.errorOccured()) {
			// There was an error.
			fail("Execution did not finish successfully. The reason is:\n"
					+ application.fetchError());
		} else {
			// Normal execution finished.
			String information = "Execution finished.\n\n";
			if (application.getHasAReturnValue()) {
				Object object = application.getReturnedObject();
				if (object == null) {
					information += "A null reference was returned.";
				} else {
					information += "An object of type "
							+ object.getClass().getName()
							+ " was returned. Its' toString()-Method said: "
							+ object.toString();
				}
			} else if (application.getThrewAnUncaughtException()) {
				// Throwable throwable = (Throwable)
				// this.application.getReturnedObject();
				Objectref objectref = (Objectref) application
						.getReturnedObject();
				 
//				 if (objectref.getName().contentEquals("java.lang.IncompatibleClassChangeError")) {
//					 Object obj = application.getReturnedObject();
//					 java.lang.Error ref = (java.lang.Error) objectref.getInitializedClass().getClass();
//					 System.out.println(ref.getStackTrace());
//				 }
				 
				// objectref.getField(field);
				// Extract the message
				fail(information + "An uncaught exception was thrown: "
						+ objectref.getName() + " " + objectref.toString()
						+ objectref.getInitializedClass().getClass());
			} else {
				information += "There was no return value.";
			}
		}
	}

}
