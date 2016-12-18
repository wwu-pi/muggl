package de.wwu.muggl.instructions.bytecode;

import java.util.Stack;

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
import de.wwu.muggl.vm.classfile.structures.attributes.elements.BootstrapMethod;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantInterfaceMethodref;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantInvokeDynamic;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantMethodHandle;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantNameAndType;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.execution.ResolutionAlgorithms;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * Implementation of the instruction <code>invokedynamic</code>.
 *
 * @author Max Schulze
 */
public class Invokedynamic extends Invoke implements Instruction {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method
	 * that the instruction belongs to is supplied as an argument.
	 *
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized
	 *         successfully, most likely due to missing additional bytes. This might be caused by a
	 *         corrupt class file, or a class file of a more recent version than what can be handled.
	 */
	public Invokedynamic(AttributeCode code) throws InvalidInstructionInitialisationException {
		super(code);
	}

	/**
	 * Make sure the method is neither the instance initializer nor the static initializer. Check
	 * that the third additional byte is zero. Set the object reference to invoke the method on as
	 * the first parameter and return its class file.
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
		// The method must be neither the instance initializer nor the static initializer.
		if (method.getName().equals(VmSymbols.OBJECT_INITIALIZER_NAME))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The Method must not be the instance initialization method.");
		if (method.getName().equals(VmSymbols.CLASS_INITIALIZER_NAME))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The Method must not be the class or interface initialization method.");

		/*
		 * The third additional byte must be zero.
		 */
		if (this.otherBytes[2] != 0 || this.otherBytes[3] != 0)
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The third and fourth operand byte must be zero.");

		// Fetch the object reference to invoke the method on.
		ReferenceValue objectref = (ReferenceValue) frame.getOperandStack().pop();
		parameters[0] = objectref;

		// Runtime exception: objectref is null.
		if (objectref == null) throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));

		// Unexpected exception: objectref is not a constant_class.
		if (!(objectref instanceof Objectref)) throw new ExecutionException("Objectref must be a reference to a Class.");

		// Fetch the class of objectref and return it.
		return objectref.getInitializedClass().getClassFile();
	}

	/**
	 * Do nothing.
	 *
	 * @param frame The currently executed frame.
	 * @param method The resolved method.
	 * @param objectrefClassFile The {@link ClassFile} of the object reference to invoke the method on.
	 */
	@Override
	protected void checkAccess(Frame frame, Method method, ClassFile objectrefClassFile) { }

	/**
	 * Select the actual method for invocation, make sure it is not abstract and check that is is
	 * public.
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
	protected Method selectMethod(Frame frame, Method method, ClassFile methodClassFile,
			ClassFile objectrefClassFile) throws ClassFileException, VmRuntimeException {
		// Does C contain a declaration for an instance method with the same name and descriptor?
		boolean methodSelected = false;
		while (!methodSelected) {
			try {
				method = objectrefClassFile.getMethodByNameAndDescriptor(method.getName(), method.getDescriptor());
				if (!method.isAccAbstract() && !method.isAccStatic()) methodSelected = true;
			} catch (MethodResolutionError e) {
				// Runtime exception: objectref does not implement the interface!
				throw new VmRuntimeException(frame.getVm().generateExc(
						"java.lang.IncompatibleClassChangeError",
						objectrefClassFile.getName() + " does not implement interface "
								+ methodClassFile.getName() + "."));
			}
			// Has the method been selected?
			if (methodSelected) break;

			// Does C have a superclass?
			if (objectrefClassFile.getSuperClass() != 0) {
				// Get the super classes recursively.
				objectrefClassFile = frame.getVm().getClassLoader().getClassAsClassFile(
						objectrefClassFile.getConstantPool()[objectrefClassFile.getSuperClass()]
								.getStringValue());
			} else {
				break;
			}
		}

		// Has the method been selected?
		if (!methodSelected)
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.AbstractMethodError",
					"The method to be invoked with " + getName() + " must not be abstract."));

		// Is it abstract?
		if (method.isAccAbstract())
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.AbstractMethodError",
					"The method to be invoked with " + getName() + " must not be abstract."));

		// Is it public?
		if (!method.isAccPublic())
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IllegalAccessError",
					"The method to be invoked with " + getName() + " must be public"));

		// Return the selected method.
		return method;
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "invokedynamic";
	}

	/**
	 * Get the number of other bytes for this instruction.
	 * @return The number of other bytes.
	 */
	@Override
	public int getNumberOfOtherBytes() {
		return 4;
	}

	/**
	 * Get a reference to the Method invoked by this instruction.
	 *
	 * @param constantPool The constant pool.
	 * @param classLoader The current class loader.
	 * @return The resolved Method.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException If the method is resolved to be the static initializer or the
	 *         fourth additional byte is not zero or the initializer or the constant pool does not
	 *         have a symbolic reference to a method at the position encoded in the bytecode.
	 * @throws NoSuchMethodError If the Method could not be found.
	 */
	public Method getInvokedMethod(Constant[] constantPool, MugglClassLoader classLoader)
		throws ClassFileException, ExecutionException {
		int index = this.otherBytes[0] << ONE_BYTE | this.otherBytes[1];
		Constant constant = constantPool[index];
		if (!(constant instanceof ConstantInterfaceMethodref))
			throw new ExecutionException(
					"3Error while executing instruction " + getName()
							+ ": Expected runtime constant pool item at index to be a symbolic reference to a method.");

		/*
		 * The third additional byte is ignored (redundant count information). Unexpected exception:
		 * The fourth additional byte must be zero.
		 */
		if (this.otherBytes[3] != 0)
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The fourth operand byte must be zero.");

		// Get the name and the descriptor.
		String[] nameAndType = ((ConstantInterfaceMethodref) constant).getNameAndTypeInfo();
		ClassFile methodClassFile = classLoader.getClassAsClassFileOrArrays(((ConstantInterfaceMethodref) constant).getClassName());
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
									"java.lang.IllegalAccessError",
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
	
	
	/**

	 * @param frame The currently executed frame.
	 * @param symbolic Toggles whether the execution is symbolic, or not.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException In case of fatal problems during the execution.
	 * @throws VmRuntimeException If runtime exceptions occur.
	 */
	@SuppressWarnings("unused")
	protected void invoke(Frame frame, boolean symbolic) throws ClassFileException,
			ExecutionException, VmRuntimeException {
		// Preparations.
		Stack<Object> stack = frame.getOperandStack();
		int index = this.otherBytes[0] << ONE_BYTE | this.otherBytes[1];
		Constant constant = frame.getConstantPool()[index];

		// check validity of constant pool entry
		if (!(constant instanceof ConstantInvokeDynamic)) {
			throw new ExecutionException(
					"1Error while executing instruction " + getName()
						+ ": Expected runtime constant pool item at index " + constant.getStringValue()
						+ "to be a symbolic reference to a call site specifier.");
		}
		ConstantInvokeDynamic constID = (ConstantInvokeDynamic) constant;
		
		ConstantNameAndType callSiteDescriptor = (ConstantNameAndType) frame.getMethod().getClassFile().getConstantPool()[constID.getNameAndTypeIndex()];
		// resolve reference to MethodHandle, MethodType and arguments from the bootstrap section
		BootstrapMethod bootstrapMethod = frame.getMethod().getClassFile().getBootstrapMethods().getBootstrapMethods()[constID.getBootstrapMethodAttrIndex()];		
		// on error with bootstrapMethod resolution -> BootstrapMethodError wrapping E

		ConstantMethodHandle bootstrapMH = (ConstantMethodHandle) frame.getConstantPool()[bootstrapMethod.getBootstrapMethodRef()];
		Constant[] bootstrapArgConst = new Constant[bootstrapMethod.getNumBootstrapArguments()];
		for (int i = 0; i< bootstrapMethod.getNumBootstrapArguments(); i++) {
			bootstrapArgConst[i] = frame.getConstantPool()[bootstrapMethod.getBootstrapArguments()[i]];
		}
		
		// construct a java.lang.invoke.MethodHandle from the methodHandle and call invoke on it
		
		// result shall be a reference to an object (of class or subclass .CallSite) <- The Call Site Object

		// then get the target of the callSiteObject
		
		
		// and execute invokevirtual java.lang.invoke.MethodHandle.invokeExact on it (descriptor of call Site specifier) 

		String[] nameAndType = getNameAndType(constant);
//		ClassFile methodClassFile = getMethodClassFile(constant, frame.getVm().getClassLoader());
//
//		// Try to resolve method from this class.
//		ResolutionAlgorithms resolution = new ResolutionAlgorithms(frame.getVm().getClassLoader());
//		Method method;
//		try {
//			if (this.getName().contains("interface")) {
//				method = resolution.resolveMethodInterface(methodClassFile, nameAndType);
//			} else
//				method = resolution.resolveMethod(methodClassFile, nameAndType);
//		} catch (ClassFileException e) {
//			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NoClassDefFoundError", e.getMessage()));
//		} catch (NoSuchMethodError e) {
//			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NoSuchMethodError", e.getMessage()));
//		}
//
//		// Prepare the parameter's array.
//		int parameterCount = method.getNumberOfArguments();
//		if (stack.size() < parameterCount)
//			throw new ExecutionException("Error while executing instruction " + getName()
//					+ ": There are less elements on the stack than parameters needed.");
//		// If it is not invokestatic the object reference is on the stack below the arguments.
//		Object[] parameters = new Object[parameterCount + this.hasObjectrefParameter];
//
//		// Get nargs arguments.
//		for (int a = parameters.length - 1; a >= this.hasObjectrefParameter; a--) {
//			parameters[a] = stack.pop();
//		}
//
//		/*
//		 * Do the checks for static/non-static methods calls and get the {@link ClassFile} of the
//		 * object reference to invoke the method on for non-static ones.
//		 */
//		ClassFile objectrefClassFile = checkStaticMethod(frame, nameAndType, method, parameters);
//
//		// Check if the access is allowed.
//		checkAccess(frame, method, objectrefClassFile);
//
//		// Select the method.
//		method = selectMethod(frame, method, methodClassFile, objectrefClassFile);
//
//		// Enter the monitor if the method is synchronized.
//		if (method.isAccSynchronized()) {
//			if (this.hasObjectrefParameter == 1) {
//				frame.getVm().getMonitorForObject((Objectref) parameters[0]).monitorEnter();
//			} else {
//				frame.getVm().getMonitorForStaticInvocation(methodClassFile).monitorEnter();
//			}
//		}
//
//		// Is the method native?
//		if (method.isAccNative()) {
//			if (Options.getInst().doNotHaltOnNativeMethods) {
//				try {
//					// Forward native methods?
//					if (Options.getInst().forwardJavaPackageNativeInvoc) {
//						/*
//						 * If required, get the object reference and drop the first parameter (the
//						 * object reference).
//						 */
//						Object[] parametersWithoutObjectref;
//						Objectref objectref = null;
//						if (this.hasObjectrefParameter == 1) {
//							objectref = (Objectref) parameters[0];
//							parametersWithoutObjectref = new Object[parameters.length - 1];
//							for (int a  = 1; a < parameters.length; a++) {
//								parametersWithoutObjectref[a - 1] = parameters[a];
//							}
//						} else {
//							parametersWithoutObjectref = parameters;
//						}
//
//						// Try to forward.
//						if (method.getClassFile().getPackageName().startsWith("java.") || method.getClassFile().getPackageName().startsWith("sun.")) {
//							NativeWrapper.forwardNativeInvocation(frame, method, methodClassFile, objectref, parametersWithoutObjectref);
//						} else if (method.getClassFile().getPackageName().equals("de.wwu.muggl.vm.execution.nativeWrapping")) {
//							// Get the object reference of the invoking method.
//							Objectref invokingObjectref = null;
//							Method invokingMethod = frame.getMethod();
//							if (!invokingMethod.isAccStatic()) {
//								invokingObjectref = (Objectref) frame.getLocalVariables()[0];
//							}
//
//							// Invoke the wrapper.
//							NativeWrapper.forwardToACustomWrapper(method, methodClassFile, parameters, invokingObjectref);
//						} else {
//							throw new ForwardingUnsuccessfulException("No wrapping handler for the native method was found.");
//						}
//						if (!frame.isHiddenFrame()
//								&& Globals.getInst().logBasedOnWhiteBlacklist(method.getPackageAndName()).orElse(true))
//							Globals.getInst().execLogger
//									.debug("Forwarded the native method " + method.getPackageAndName() + " to a wrapper.");
//						
//						// Release the monitor if it is synchronized.
//						if (method.isAccSynchronized()) {
//							if (this.hasObjectrefParameter == 1) {
//								frame.getVm().getMonitorForObject(objectref).monitorExit();
//							} else {
//								frame.getVm().getMonitorForStaticInvocation(methodClassFile).monitorExit();
//							}
//						}
//						if (!frame.isHiddenFrame()
//								&& Globals.getInst().logBasedOnWhiteBlacklist(method.getPackageAndName()).orElse(true))
//							Globals.getInst().executionInstructionLogger
//									.debug("upon return: (op: " + frame.getOperandStack() + ", localvar: localvar: [" + Arrays.stream(frame.getLocalVariables())
//									                                   									.map(x -> (x == null)? "null": x.toString()).collect(Collectors.joining(", "))									
//									                                   									+ "] pc: " + frame.getPc() + ")");
//
//						// Finished.
//						return;
//					}
//				} catch (ForwardingUnsuccessfulException e) {
//					// Ignore it, but log it.
//					if (!frame.isHiddenFrame())
//						Globals.getInst().execLogger.warn(
//								"Forwarding of the native method " + method.getPackageAndName()
//								+ " was not successfull. The reason is: " + e.getMessage());
//				}
//				/*
//				 * Either push a zero / null value for the native method's return type, or
//				 * completely ignore it.
//				 */
//				if (Options.getInst().assumeNativeReturnValuesToBeZeroNull) {
//					pushZeroOrNull(stack, method, symbolic);
//					if (!frame.isHiddenFrame())
//						Globals.getInst().execLogger.debug(
//								"Assume a null/zero value for the native method " + method.getPackageAndName() + ".");
//				} else {
//					if (!frame.isHiddenFrame())
//						Globals.getInst().execLogger.info(
//								"Skipping the native method " + method.getPackageAndName() + ".");
//				}
//
//				// Release the monitor if it is synchronized.
//				if (method.isAccSynchronized()) {
//					if (this.hasObjectrefParameter == 1) {
//						frame.getVm().getMonitorForObject((Objectref) parameters[0]).monitorExit();
//					} else {
//						frame.getVm().getMonitorForStaticInvocation(methodClassFile).monitorExit();
//					}
//				}
//
//				// Finished.
//				return;
//			}
//
//			// The method is native but there is no way of handling it.
//			throw new ExecutionException("Error while executing instruction " + getName()
//					+ ": Execution of native methods is impossible.");
//		}
//
//		// Save current frame...
//		frame.setPc(frame.getVm().getPc() + 1 + this.getNumberOfOtherBytes());
//		frame.getVm().getStack().push(frame);
//
//		// Push new one.
//		frame.getVm().createAndPushFrame(frame, method, parameters);
//
//		// Finish.
//		frame.getVm().setReturnFromCurrentExecution(true);
	}
	
	/**
	 * Get the corresponding class file for a constant_interfacemethodref.
	 *
	 * @param constant A constant_interfacemethodref.
	 * @param classLoader The class loader.
	 * @return The corresponding class file for a constant_interfacemethodref.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException In case of any other problems.
	 */
	@Override
	protected ClassFile getMethodClassFile(Constant constant, MugglClassLoader classLoader) throws ClassFileException, ExecutionException {
		// FIXME mxs: why override the parent's method if they're identical?
		if (!(constant instanceof ConstantInterfaceMethodref)) {
			throw new ExecutionException(
					"2Error while executing instruction " + getName()
						+ ": Expected runtime constant pool item at index " + constant.getValue()
						+ "to be a symbolic reference to a method.");
		}
		
		return classLoader.getClassAsClassFileOrArrays(
				((ConstantInterfaceMethodref) constant).getClassName());
	}

}
