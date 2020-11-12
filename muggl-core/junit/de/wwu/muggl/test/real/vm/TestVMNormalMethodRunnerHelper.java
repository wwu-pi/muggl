package de.wwu.muggl.test.real.vm;

import static org.junit.Assert.fail;

import java.lang.invoke.MethodType;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * Helps executing a single method.
 * 
 * Shall print Errors on stack trace and raise according exceptions or otherwise finish gracefully.
 * 
 * @author Max Schulze
 *
 */
public class TestVMNormalMethodRunnerHelper {

	public static void runMethodNoArgVoid(MugglClassLoader classLoader, final String classFileName,
			final String methodName) throws ClassFileException, InitializationException, InterruptedException {
		Options.getInst().symbolicMode = false;
		
		ClassFile classFile = classLoader.getClassAsClassFile(classFileName, true);

		Method method = classFile.getMethodByNameAndDescriptor(methodName, "()V");

		Application application = new Application(classLoader, classFile.getName(), method);
		application.start();

		while (!application.getExecutionFinished()) {
			Thread.sleep(Globals.SAFETY_SLEEP_DELAY);
		}

		// Find out if execution finished successfully.
		if (application.errorOccured()) {
			// There was an error.
			fail("Execution did not finish successfully. The reason is:\n" + application.fetchError());
		} else {
			if (application.getHasAReturnValue()) {
				return;
			} else if (application.getThrewAnUncaughtException()) {
				Objectref objectref = (Objectref) application.getReturnedObject();

				ClassFile throwableClassFile = objectref.getInitializedClass().getClassFile();
				Field detailMessageField = throwableClassFile.getFieldByName("detailMessage", true);
				ClassFile stringClassFile = application.getVirtualMachine().getClassLoader()
						.getClassAsClassFile("java.lang.String");
				Field stringValueField = stringClassFile.getFieldByNameAndDescriptor("value", "[C");
				Objectref stringObjectref = (Objectref) objectref.getField(detailMessageField);
				String message;
				if (stringObjectref == null) {
					message = "null";
				} else {
					Arrayref arrayref = (Arrayref) stringObjectref.getField(stringValueField);

					// Convert it.
					char[] characters = new char[arrayref.getLength()];
					for (int a = 0; a < arrayref.getLength(); a++) {
						characters[a] = (Character) arrayref.getElement(a);
					}
					message = new String(characters);
				}

				fail("Uncaught exception, no suitable exception handler: "
						+ throwableClassFile.getName().replace("/", ".") + " (" + message + ")");

			} else {
			}
		}
		return;
	}

	public static Object runMethod(MugglClassLoader classLoader, final String classFileName, final String methodName,
			final MethodType methodType, final Object[] args)
			throws ClassFileException, InitializationException, InterruptedException {
		Options.getInst().symbolicMode = false;
		
		ClassFile classFile = classLoader.getClassAsClassFile(classFileName, true);

		Method method = classFile.getMethodByNameAndDescriptor(methodName, methodType.toMethodDescriptorString());

		Application application = new Application(classLoader, classFile.getName(), method);

		if (args != null) {
			Object[] modifArgs = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof String) {
					// if any of the parameters is a string, we have to take care of wrapping them as objectrefs from
					// the StringCache!
					modifArgs[i] = application.getVirtualMachine().getStringCache()
							.getStringObjectref((String) args[i]);
				} else
					modifArgs[i] = args[i];
			}
			method.setPredefinedParameters(modifArgs);
		}

		application.start();

		while (!application.getExecutionFinished()) {
			Thread.sleep(Globals.SAFETY_SLEEP_DELAY);
		}

		// Find out if execution finished successfully.
		if (application.errorOccured()) {
			// There was an error.
			fail("Execution did not finish successfully. The reason is:\n" + application.fetchError());
			application.finalize();
		} else {
			if (application.getHasAReturnValue()) {
				// native type or wrapped?
				Object object = application.getReturnedObject();
				application.finalizeApplication();
				if (object == null)
					return object;
				if (object instanceof ReferenceValue) {
					try {
						return new MugglToJavaConversion(application.getVirtualMachine()).toJava(object);
					} catch (ConversionException e) {
						e.printStackTrace();
					}
				} else {
					// possibly to some extending / boxing becuase of ireturn statements that could be meant for a
					// different type
					if (object.getClass().getName().equals("java.lang.Integer")) {
						switch (method.getReturnType()) {
						case "java.lang.Boolean":
						case "boolean":
							return (((Integer) object).intValue() == 1);
						case "java.lang.Byte":
						case "byte":
							return ((Integer) object).byteValue();
						case "java.lang.Short":
						case "short":
							return ((Integer) object).shortValue();
						case "java.lang.Character":
						case "char":
							return (char) ((Integer) object).intValue();
						default:
							return object;
						}
					}
					return object;
				}

			} else if (application.getThrewAnUncaughtException()) {
				Objectref objectref = (Objectref) application.getReturnedObject();

				ClassFile throwableClassFile = objectref.getInitializedClass().getClassFile();
				Field detailMessageField = throwableClassFile.getFieldByName("detailMessage", true);
				ClassFile stringClassFile = application.getVirtualMachine().getClassLoader()
						.getClassAsClassFile("java.lang.String");
				Field stringValueField = stringClassFile.getFieldByNameAndDescriptor("value", "[C");
				Objectref stringObjectref = (Objectref) objectref.getField(detailMessageField);
				String message;
				if (stringObjectref == null) {
					message = "null";
				} else {
					Arrayref arrayref = (Arrayref) stringObjectref.getField(stringValueField);

					// Convert it.
					char[] characters = new char[arrayref.getLength()];
					for (int a = 0; a < arrayref.getLength(); a++) {
						characters[a] = (Character) arrayref.getElement(a);
					}
					message = new String(characters);
				}

				fail("Uncaught exception, no suitable exception handler: "
						+ throwableClassFile.getName().replace("/", ".") + " (" + message + ")");

			} else {
			}
		}

		return null;
	}
	
	public static Object runMethodSymbolic(MugglClassLoader classLoader, final String classFileName, final String methodName,
			final MethodType methodType, final Object[] args)
			throws ClassFileException, InitializationException, InterruptedException {
		Options.getInst().symbolicMode = true;
		
		ClassFile classFile = classLoader.getClassAsClassFile(classFileName, true);

		Method method = classFile.getMethodByNameAndDescriptor(methodName, methodType.toMethodDescriptorString());

		Application application = new Application(classLoader, classFile.getName(), method);

		if (args != null) {
			Object[] modifArgs = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof String) {
					// if any of the parameters is a string, we have to take care of wrapping them as objectrefs from
					// the StringCache!
					modifArgs[i] = application.getVirtualMachine().getStringCache()
							.getStringObjectref((String) args[i]);
				} else
					modifArgs[i] = args[i];
			}
			method.setPredefinedParameters(modifArgs);
		}

		application.start();

		while (!application.getExecutionFinished()) {
			Thread.sleep(Globals.SAFETY_SLEEP_DELAY);
		}

		// Find out if execution finished successfully.
		if (application.errorOccured()) {
			// There was an error.
			fail("Execution did not finish successfully. The reason is:\n" + application.fetchError());
			application.finalize();
		} else {
			if (application.getHasAReturnValue()) {
				// native type or wrapped?
				Object object = application.getReturnedObject();
				application.finalizeApplication();
				if (object == null)
					return object;
				if (object instanceof ReferenceValue) {
					try {
						return new MugglToJavaConversion(application.getVirtualMachine()).toJava(object);
					} catch (ConversionException e) {
						e.printStackTrace();
					}
				} else {
					// possibly to some extending / boxing becuase of ireturn statements that could be meant for a
					// different type
					if (object.getClass().getName().equals("java.lang.Integer")) {
						switch (method.getReturnType()) {
						case "java.lang.Boolean":
						case "boolean":
							return (((Integer) object).intValue() == 1);
						case "java.lang.Byte":
						case "byte":
							return ((Integer) object).byteValue();
						case "java.lang.Short":
						case "short":
							return ((Integer) object).shortValue();
						case "java.lang.Character":
						case "char":
							return (char) ((Integer) object).intValue();
						default:
							return object;
						}
					}
					return object;
				}

			} else if (application.getThrewAnUncaughtException()) {
				Objectref objectref = (Objectref) application.getReturnedObject();

				ClassFile throwableClassFile = objectref.getInitializedClass().getClassFile();
				Field detailMessageField = throwableClassFile.getFieldByName("detailMessage", true);
				ClassFile stringClassFile = application.getVirtualMachine().getClassLoader()
						.getClassAsClassFile("java.lang.String");
				Field stringValueField = stringClassFile.getFieldByNameAndDescriptor("value", "[C");
				Objectref stringObjectref = (Objectref) objectref.getField(detailMessageField);
				String message;
				if (stringObjectref == null) {
					message = "null";
				} else {
					Arrayref arrayref = (Arrayref) stringObjectref.getField(stringValueField);

					// Convert it.
					char[] characters = new char[arrayref.getLength()];
					for (int a = 0; a < arrayref.getLength(); a++) {
						characters[a] = (Character) arrayref.getElement(a);
					}
					message = new String(characters);
				}

				fail("Uncaught exception, no suitable exception handler: "
						+ throwableClassFile.getName().replace("/", ".") + " (" + message + ")");

			} else {
			}
		}

		return null;
	}

}
