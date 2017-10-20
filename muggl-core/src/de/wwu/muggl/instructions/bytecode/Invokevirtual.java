package de.wwu.muggl.instructions.bytecode;

import java.util.LinkedList;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.MethodResolutionError;
import de.wwu.muggl.instructions.general.Invoke;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantMethodref;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.BoxingConversion;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.execution.ResolutionAlgorithms;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.ReferenceValue;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * Implementation of the instruction <code>invokevirtual</code>.
 *
 * @author Tim Majchrzak, Max Schulze
 */
public class Invokevirtual extends Invoke implements Instruction {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method
	 * that the instruction belongs to is supplied as an argument.
	 *
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized
	 *         successfully, most likely due to missing additional bytes. This might be caused by a
	 *         corrupt class file, or a class file of a more recent version than what can be handled.
	 */
	public Invokevirtual(AttributeCode code) throws InvalidInstructionInitialisationException {
		super(code);
	}

	/**
	 * Make sure the method is neither the instance initializer nor the static initializer. Set the
	 * object reference to invoke the method on as the first parameter and return its class file.
	 *
	 * @param frame The currently executed frame.
	 * @param nameAndType The name and the descriptor of the method.
	 * @param method The resolved method.
	 * @param parameters The (yet unfilled) array of parameters.
	 * @return The {@link ClassFile} of the object reference to invoke the method on.
	 * @throws ExecutionException If an unexpected problem is found that does not throw a runtime
	 *         exception but forces the virtual machine to halt.
	 * @throws VmRuntimeException If an unexpected condition it met and a runtime exception is
	 *         thrown.
	 */
	@Override
	protected ClassFile checkStaticMethod(Frame frame, String[] nameAndType,
			Method method, Object[] parameters) throws ExecutionException, VmRuntimeException {
		checkNoInstanceInit(method);

		Object rawRefVAl = frame.getOperandStack().pop();
		
		// Runtime exception: objectref is null.
		if (rawRefVAl == null) 
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException", "checkStaticMethod in Invokevirtual " + nameAndType[0]+ " "+ nameAndType[1]));
		
		ReferenceValue objectref = null;
		if (rawRefVAl instanceof ReferenceValue) {
			// Fetch the object reference to invoke the method on.
			objectref = (ReferenceValue) rawRefVAl;
		} else {
			objectref = BoxingConversion.Boxing(frame.getVm(), rawRefVAl);
			if (objectref == null) {
				try {
					// Not a primitive, not an Objectref - must be an *actual* object! (surprise...). Wrap it.
					final MugglToJavaConversion converter = new MugglToJavaConversion(frame.getVm());
					objectref = (ReferenceValue) converter.toMuggl(rawRefVAl, false);
				} catch (ConversionException e) {
					e.printStackTrace();
					// Wrapping failed.
					throw new VmRuntimeException(frame.getVm().generateExc("java.lang.RuntimeException", "To-objectref conversion failed in checkStaticMethod in Invokevirtual " + nameAndType[0]+ " "+ nameAndType[1]));
				}
			}
		}
		parameters[0] = objectref;

		if (objectref instanceof Arrayref) {
			// does not really make sense to return the referenceType here when you invoke things like
			// .clone() on arrays
			try {
				return frame.getVm().getClassLoader().getClassAsClassFile("java.util.Arrays");
			} catch (ClassFileException e) {
				e.printStackTrace();
			}

		}
		// Fetch the class of objectref and return it.
		return objectref.getInitializedClass().getClassFile();
	}

	/**
	 * Perform access checks. If the method is protected elaborate checks are needed. Check that the
	 * method is not static.
	 *
	 * @param frame The currently executed frame.
	 * @param method The resolved method.
	 * @param objectrefClassFile The {@link ClassFile} of the object reference to invoke the method on.
	 * @throws ClassFileException If a required class file cannot be loaded.
	 * @throws VmRuntimeException If an unexpected condition it met and a runtime exception is
	 *         thrown.
	 */
	@Override
	protected void checkAccess(Frame frame, Method method, ClassFile objectrefClassFile)
			throws ClassFileException, VmRuntimeException {
		// Elaborate checks for protected methods.
		if (method.isAccProtected()) {
			boolean isMemberOfClassOrSuperclass = false;
			// Check if the method is a member of the current class or of a superclass of the current class.
			if (method.getClassFile().getName().equals(frame.getMethod().getClassFile().getName())) {
				isMemberOfClassOrSuperclass = true;
			} else {
				ClassFile methodClassFileSuperClass = frame.getMethod().getClassFile();
				while (methodClassFileSuperClass.getSuperClass() != 0) {
					methodClassFileSuperClass = frame.getVm().getClassLoader().getClassAsClassFile(
							methodClassFileSuperClass.getConstantPool()[methodClassFileSuperClass
									.getSuperClass()].getStringValue());
					if (method.getClassFile().getName().equals(methodClassFileSuperClass.getName())) {
						isMemberOfClassOrSuperclass = true;
						break;
					}
				}
			}

			// Do further checking.
			if (isMemberOfClassOrSuperclass) {
				// Is objectref the current class?
				if (!method.getClassFile().getName().equals(objectrefClassFile.getName())) {
					// Objectref is not the same class. Is it a subclass then.
					boolean classMatchFound = false;
					ClassFile objectrefClassFileSuperClass = objectrefClassFile;
					while (objectrefClassFileSuperClass.getSuperClass() != 0) {
						objectrefClassFileSuperClass = frame.getVm().getClassLoader().getClassAsClassFile(
										objectrefClassFileSuperClass.getConstantPool()[objectrefClassFileSuperClass
												.getSuperClass()].getStringValue());
						if (method.getClassFile().getName().equals(objectrefClassFileSuperClass.getName())) {
							// Found the match!
							classMatchFound = true;
							break;
						}
					}
					// Runtime exception.
					if (!classMatchFound)
						throw new VmRuntimeException(frame.getVm().generateExc(
								"java.lang.IllegalAccessError",
								objectrefClassFile.getName() + " may not access method "
										+ method.getName() + " in class "
										+ frame.getMethod().getClassFile().getName() + "."));
				}
			}
		}

		// Is it static?
		if (method.isAccStatic())
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IncompatibleClassChangeError",
					"The method to be invoked with " + getName() + " must not be static."));
	}

	/**
	 * Select the actual method for invocation and make sure it is not abstract.
	 *
	 * @param frame The currently executed frame.
	 * @param method The resolved method.
	 * @param methodClassFile The method's class file according to its name and descriptor.
	 * @param objectrefClassFile The {@link ClassFile} of the object reference to invoke the method
	 *        on.
	 * @return The selected method. Might be the method specified.
	 * @throws ClassFileException If a required class file cannot be loaded.
	 * @throws VmRuntimeException If an unexpected condition it met and a runtime exception is
	 *         thrown.
	 */
	@Override
	protected Method selectMethod(final Frame frame, Method method, ClassFile methodClassFile,
			final ClassFile objectrefClassFile) throws ClassFileException, VmRuntimeException {
		Globals.getInst().execLogger.trace("invokevirtual.selectMethod " + method.getFullNameWithParameterTypesAndNames());
		Method selectedMethod = null;
		if (!method.isSignaturePolymorphic()) {
			// 1. If C contains a declaration for an instance method m that
			// overrides (§5.4.5) the resolved method, then m is the method
			// to be invoked
			selectedMethod = objectrefClassFile.getMethodByNameAndDescriptorOrNull(method.getName(),
					method.getDescriptor());

			// 2. Otherwise, if C has a superclass, a search for a declaration
			// of an instance method that overrides the resolved method
			// is performed, starting with the direct superclass of C and
			// continuing with the direct superclass of that class, and so forth,
			// until an overriding method is found or no further superclasses
			// exist. If an overriding method is found, it is the method to be
			// invoked.
			if (selectedMethod == null) {
				ClassFile classFile1 = objectrefClassFile;
				while (classFile1.getSuperClass() != 0) {

					classFile1 = frame.getVm().getClassLoader().getClassAsClassFile(
							classFile1.getConstantPool()[classFile1.getSuperClass()].getStringValue());

					selectedMethod = classFile1.getMethodByNameAndDescriptorOrNull(method.getName(),
							method.getDescriptor());

					if (selectedMethod != null) {
						Globals.getInst().execLogger.trace("Lookup of " + method.getName() + " in super class "
								+ classFile1.getName() + " succeeded.");
						break;
					} else {
						Globals.getInst().execLogger.trace("Lookup of " + method.getName() + " in super class "
								+ classFile1.getName() + " unsuccessfull. Trying its super classes.");
					}
				}
			}

			if (selectedMethod != null) {
				checkAccess(frame, selectedMethod, objectrefClassFile);
			} else {

			// Otherwise, if there is exactly one maximally-specific method
			// (§5.4.3.3) in the superinterfaces of C that matches the resolved
			// method's name and descriptor and is not abstract , then it is
			// the method to be invoked.

			// Now on to interfaces
			LinkedList<String> superInterfaces = new LinkedList<>();
			LinkedList<String> exploreSuperClasses = new LinkedList<>();

			// add self as a starting class
			exploreSuperClasses.add(objectrefClassFile.getName());

			// Trying the super interfaces recursively
			// wanting to find the maximally-specific superinterface methods
			// that match name and descriptor and that has neither its
			// ACC_PRIVATE flag nor its ACC_STATIC flag set

			while (!superInterfaces.isEmpty() || !exploreSuperClasses.isEmpty()) {

				final int ifaces = superInterfaces.size();
				for (int i = 0; i < ifaces; i++) {
					ClassFile classFile1 = null;
					try {
						classFile1 = frame.getVm().getClassLoader().getClassAsClassFile(superInterfaces.pop());
						selectedMethod = classFile1.getMethodByNameAndDescriptor(method.getName(), method.getDescriptor());
						Globals.getInst().execLogger.trace(
								"Lookup of " + method.getName() + " in class " + classFile1.getName() + " succeeded.");
						break;
					} catch (MethodResolutionError e1) {
						Globals.getInst().execLogger.trace("Lookup of " + method.getName() + " in interface class "
								+ classFile1.getClassName() + " unsuccessfull. Enqueueing its super class.");
						if (classFile1.getSuperClass() != 0)
							exploreSuperClasses
									.add(classFile1.getConstantPool()[classFile1.getSuperClass()].getStringValue());
						for (int iface : classFile1.getInterfaces()) {
							superInterfaces.add(classFile1.getConstantPool()[iface].getStringValue());
						}
					}
				}
				if (selectedMethod != null)
					break;

				final int sClasses = exploreSuperClasses.size();
				for (int i = 0; i < sClasses; i++) {
					ClassFile classFile1 = null;
					try {
						classFile1 = frame.getVm().getClassLoader().getClassAsClassFile(exploreSuperClasses.pop());
						selectedMethod = classFile1.getMethodByNameAndDescriptor(method.getName(), method.getDescriptor());
						Globals.getInst().execLogger.trace(
								"Lookup of " + method.getName() + " in class " + classFile1.getName() + " succeeded.");
						break;
					} catch (MethodResolutionError e1) {
						Globals.getInst().execLogger
								.trace("Lookup of " + method.getDescriptor() + " in class " + classFile1.getClassName()
										+ " unsuccessfull. Enqueueing its super class and interfaces.");
						if (classFile1.getSuperClass() != 0)
							exploreSuperClasses
									.add(classFile1.getConstantPool()[classFile1.getSuperClass()].getStringValue());
						for (int iface : classFile1.getInterfaces()) {
							superInterfaces.add(classFile1.getConstantPool()[iface].getStringValue());
						}
					}
				}

				if (selectedMethod != null)
					break;
			}
			}
		} else {
			throw new java.lang.invoke.WrongMethodTypeException("invokevirtual does not currently support signature polymorphism");
		}

		// Has the method been selected?
		if (selectedMethod == null)
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.AbstractMethodError",
					"Error while getting method " + method.getName() + " to be invoked with " + getName() + ""));
		method = selectedMethod;

		// Is it abstract?
		if (method.isAccAbstract())
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.AbstractMethodError",
					"The method " + method.getName() + " to be invoked with " + getName() + " must not be abstract."));

		// Return the selected method.
		return method;
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "invokevirtual";
	}

	/**
	 * Get a reference to the Method invoked by this instruction.
	 * @param constantPool The constant pool.
	 * @param classLoader The current class loader.
	 * @return The resolved Method.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException If the method is resolved to be the static initializer or the initializer or the constant pool does not have a symbolic reference to a method at the position encoded in the bytecode.
	 * @throws NoSuchMethodError If the Method could not be found.
	 */
	public Method getInvokedMethod(Constant[] constantPool, MugglClassLoader classLoader)
		throws ClassFileException, ExecutionException {
		int index = this.otherBytes[0] << ONE_BYTE | this.otherBytes[1];
		Constant constant = constantPool[index];
		if (!(constant instanceof ConstantMethodref))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": Expected runtime constant pool item at index to be a symbolic reference to a method.");

		// Get the name and the descriptor.
		String[] nameAndType = ((ConstantMethodref) constant).getNameAndTypeInfo();
		ClassFile methodClassFile = classLoader.getClassAsClassFileOrArrays(((ConstantMethodref) constant).getClassName());
		if (nameAndType[0].equals(VmSymbols.OBJECT_INITIALIZER_NAME))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The Method must not be the instance initialization method.");
		if (nameAndType[0].equals(VmSymbols.CLASS_INITIALIZER_NAME))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The Method must not be the class or interface initialization method.");

		// Try to resolve method from this class.
		ResolutionAlgorithms resoluton = new ResolutionAlgorithms(classLoader);
		return resoluton.resolveMethod(methodClassFile, nameAndType);
	}

	/**
	 * Get the thrown exception types as fully qualified java names.
	 * @return The thrown exception types.
	 */
	public String[] getThrownExceptionTypes() {
		// UnsatisfiedLinkError is not thrown by this vm implementation.
		String[] exceptionTypes = { "java.lang.AbstractMethodError",
									"java.lang.ExceptionInInitializerError",
									"java.lang.IncompatibleClassChangeError",
									"java.lang.InstantiationException",
									"java.lang.InvocationTargetException",
									"java.lang.NoClassDefFoundError",
									"java.lang.NoSuchMethodError",
									"java.lang.NullPointerException"};
		return exceptionTypes;
	}

	/**
	 * Get the number of elements that will be popped from the stack when this instruction is
	 * executed.
	 *
	 * @return The number of elements that will be popped from the stack.
	 */
	public int getNumberOfPoppedElements() {
		return 1;
	}

}
