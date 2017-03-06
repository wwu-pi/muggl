package de.wwu.muggl.instructions.general;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.interfaces.control.JumpException;
import de.wwu.muggl.instructions.interfaces.control.JumpInvocation;
import de.wwu.muggl.instructions.interfaces.data.StackPop;
import de.wwu.muggl.instructions.interfaces.data.VariableDefining;
import de.wwu.muggl.instructions.interfaces.data.VariableUsing;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.UndefinedValue;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantMethodref;
import de.wwu.muggl.vm.exceptions.ExceptionHandler;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.execution.ForwardingUnsuccessfulException;
import de.wwu.muggl.vm.execution.NativeWrapper;
import de.wwu.muggl.vm.execution.ResolutionAlgorithms;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.exceptions.SymbolicExceptionHandler;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muggl.solvers.expressions.DoubleConstant;
import de.wwu.muggl.solvers.expressions.FloatConstant;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.LongConstant;

/**
 * Abstract instruction with some concrete methods for invocation instructions. Concrete
 * instructions can be extended from this class.<br />
 * <br />
 * This class implements {@link VariableDefining}. The instructions inherited from it does however
 * not define variables in the currently executed method but in the method that will be invoked by
 * them.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-12-15
 */
public abstract class Invoke extends GeneralInstructionWithOtherBytes implements JumpException,
		JumpInvocation, StackPop, VariableDefining, VariableUsing {
	/**
	 * This field takes a value of 1 if an invocation takes an object reference parameter.
	 */
	protected int hasObjectrefParameter;

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method
	 * that the instruction belongs to is supplied as an argument.
	 *
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized
	 *         successfully, most likely due to missing additional bytes. This might be caused by a
	 *         corrupt class file, or a class file of a more recent version than what can be handled.
	 */
	public Invoke(AttributeCode code) throws InvalidInstructionInitialisationException {
		super(code);
		this.hasObjectrefParameter = 1;
	}

	/**
	 * Execute the instruction.
	 *
	 * @param frame The currently executed frame.
	 * @throws ExecutionException In case of fatal problems during the execution.
	 */
	@Override
	public void execute(Frame frame) throws ExecutionException {
		try {
			invoke(frame, false);
		} catch (VmRuntimeException e) {
			ExceptionHandler handler = new ExceptionHandler(frame, e);
			try {
				handler.handleException();
			} catch (ExecutionException e2) {
				executionFailed(e2);
			}
		} catch (ClassFileException e) {
			executionFailed(e);
		} catch (ExecutionException e) {
			executionFailed(e);
		}
	}

	/**
	 * Execute the instruction symbolically.
	 *
	 * @param frame The currently executed frame.
	 * @throws NoExceptionHandlerFoundException If no handler could be found.
	 * @throws SymbolicExecutionException In case of fatal problems during the symbolic execution.
	 */
	@Override
	public void executeSymbolically(Frame frame) throws NoExceptionHandlerFoundException, SymbolicExecutionException {
		try {
			invoke(frame, true);
		} catch (VmRuntimeException e) {
			SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, e);
			try {
				handler.handleException();
			} catch (ExecutionException e2) {
				executionFailedSymbolically(e2);
			}
		} catch (ClassFileException e) {
			executionFailedSymbolically(e);
		} catch (ExecutionException e) {
			executionFailedSymbolically(e);
		}
	}

	/**
	 * Invoke a method. This method encapsulates the whole invocation functionality and call methods
	 * specific to the distinct invocation instructions.
	 *
	 * @param frame The currently executed frame.
	 * @param symbolic Toggles whether the execution is symbolic, or not.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException In case of fatal problems during the execution.
	 * @throws VmRuntimeException If runtime exceptions occur.
	 */
	protected void invoke(Frame frame, boolean symbolic) throws ClassFileException,
			ExecutionException, VmRuntimeException {
		// Preparations.
		Stack<Object> stack = frame.getOperandStack();
		int index = this.otherBytes[0] << ONE_BYTE | this.otherBytes[1];
		Constant constant = frame.getConstantPool()[index];

		// Get the name and the descriptor.
		String[] nameAndType = getNameAndType(constant);
		ClassFile methodClassFile = getMethodClassFile(constant, frame.getVm().getClassLoader());

		// Try to resolve method from this class.
		ResolutionAlgorithms resolution = new ResolutionAlgorithms(frame.getVm().getClassLoader());
		Method method;
		try {
			if (this.getName().contains("interface")) {
				method = resolution.resolveMethodInterface(methodClassFile, nameAndType);
			} else
				method = resolution.resolveMethod(methodClassFile, nameAndType);
		} catch (ClassFileException e) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NoClassDefFoundError", e.getMessage()));
		} catch (NoSuchMethodError e) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NoSuchMethodError", e.getMessage()));
		}

		// Prepare the parameter's array.
		int parameterCount = method.getNumberOfArguments();
		if (stack.size() < parameterCount)
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": There are less elements on the stack than parameters needed.");
		// If it is not invokestatic the object reference is on the stack below the arguments.
		Object[] parameters = new Object[parameterCount + this.hasObjectrefParameter];

		// Get nargs arguments.
		for (int a = parameters.length - 1; a >= this.hasObjectrefParameter; a--) {
			parameters[a] = stack.pop();
		}

		if(parameters.length>0 && parameters[0]==null && nameAndType[0].equals("loadClass")) {
			// FIXME TODO mxs: hard fake. Because the SystemClassLoader is not known
			String className = frame.getVm().getStringCache().getStringObjrefValue((Objectref)parameters[1]);
			ClassFile klass = frame.getVm().getClassLoader().getClassAsClassFile(className);
			klass.getTheInitializedClass(frame.getVm());
			frame.getOperandStack().push(klass.getMirrorJava());
			return;
		}
		
		/*
		 * Do the checks for static/non-static methods calls and get the {@link ClassFile} of the
		 * object reference to invoke the method on for non-static ones.
		 */
		ClassFile objectrefClassFile = checkStaticMethod(frame, nameAndType, method, parameters);

		// Check if the access is allowed.
		checkAccess(frame, method, objectrefClassFile);

		// Select the method.
		method = selectMethod(frame, method, methodClassFile, objectrefClassFile);

		// Enter the monitor if the method is synchronized.
		if (method.isAccSynchronized()) {
			if (this.hasObjectrefParameter == 1) {
				frame.getVm().getMonitorForObject((Objectref) parameters[0]).monitorEnter();
			} else {
				frame.getVm().getMonitorForStaticInvocation(methodClassFile).monitorEnter();
			}
		}

		
		// Is the method native?
		if (method.isAccNative()) {
			if (Options.getInst().doNotHaltOnNativeMethods) {
				try {
					// Forward native methods?
					if (Options.getInst().forwardJavaPackageNativeInvoc) {
						/*
						 * If required, get the object reference and drop the first parameter (the
						 * object reference).
						 */
						Object[] parametersWithoutObjectref;
						ReferenceValue objectref = null; // could be Arrayref or Objectref
						if (this.hasObjectrefParameter == 1) {
							objectref = (ReferenceValue) parameters[0];
							parametersWithoutObjectref = new Object[parameters.length - 1];
							for (int a  = 1; a < parameters.length; a++) {
								parametersWithoutObjectref[a - 1] = parameters[a];
							}
						} else {
							parametersWithoutObjectref = parameters;
						}

						// Try to forward.
						if (method.getClassFile().getPackageName().startsWith("java.") || method.getClassFile().getPackageName().startsWith("sun.")) {
							NativeWrapper.forwardNativeInvocation(frame, method, methodClassFile, objectref, parametersWithoutObjectref);
						} else if (method.getClassFile().getPackageName().equals("de.wwu.muggl.vm.execution.nativeWrapping")) {
							// Get the object reference of the invoking method.
							Objectref invokingObjectref = null;
							Method invokingMethod = frame.getMethod();
							if (!invokingMethod.isAccStatic()) {
								invokingObjectref = (Objectref) frame.getLocalVariables()[0];
							}

							// Invoke the wrapper.
							Object returnval = NativeWrapper.forwardToACustomWrapper(method, methodClassFile, parameters, invokingObjectref);
							if (!(returnval instanceof UndefinedValue)) {
								frame.getOperandStack().push(returnval);
							}
						} else {
							throw new ForwardingUnsuccessfulException("No wrapping handler for the native method " + method.getFullNameWithParameterTypesAndNames() + " was found.");
						}
						if (!frame.isHiddenFrame()
								&& Globals.getInst().logBasedOnWhiteBlacklist(method.getPackageAndName()).orElse(true))
							Globals.getInst().execLogger
									.debug("Forwarded the native method1 " + method.getPackageAndName() + " to a wrapper.");
						
						// Release the monitor if it is synchronized.
						if (method.isAccSynchronized()) {
							if (this.hasObjectrefParameter == 1) {
								frame.getVm().getMonitorForObject((Objectref) objectref).monitorExit();
							} else {
								frame.getVm().getMonitorForStaticInvocation(methodClassFile).monitorExit();
							}
						}
						if (!frame.isHiddenFrame()
								&& Globals.getInst().logBasedOnWhiteBlacklist(method.getPackageAndName()).orElse(true))
							Globals.getInst().executionInstructionLogger
									.debug("upon return: (op: " + frame.getOperandStack() + ", localvar: [" + Arrays.stream(frame.getLocalVariables())
									                                   									.map(x -> (x == null)? "null": x.toString()).collect(Collectors.joining(", "))									
									                                   									+ "] pc: " + frame.getPc() + ")");

						// Finished.
						return;
					}
				} catch (ForwardingUnsuccessfulException e) {
					// Ignore it, but log it.
					if (!frame.isHiddenFrame()){
						Globals.getInst().execLogger.warn(
								"Forwarding of the native method " + method.getPackageAndName()
								+ " was not successfull. The reason is: " + e.getMessage());
						
						frame.getVm().fillDebugStackTraces();
						Globals.getInst().execLogger.debug(frame.getVm().debugStackTraceMugglVM);
						
					}
				}
				/*
				 * Either push a zero / null value for the native method's return type, or
				 * completely ignore it.
				 */
				//TODO don't native methods *also* write return values? check for conflicts. -JD
				if (Options.getInst().assumeNativeReturnValuesToBeZeroNull) {
					pushZeroOrNull(stack, method, symbolic);
					if (!frame.isHiddenFrame())
						Globals.getInst().execLogger.debug(
								"Assume a null/zero value for the native method " + method.getPackageAndName() + ".");
				} else {
					if (!frame.isHiddenFrame())
						Globals.getInst().execLogger.info(
								"Skipping the native method " + method.getPackageAndName() + ".");
				}

				// Release the monitor if it is synchronized.
				if (method.isAccSynchronized()) {
					if (this.hasObjectrefParameter == 1) {
						frame.getVm().getMonitorForObject((Objectref) parameters[0]).monitorExit();
					} else {
						frame.getVm().getMonitorForStaticInvocation(methodClassFile).monitorExit();
					}
				}

				// Finished.
				return;
			}

			// The method is native but there is no way of handling it.
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": Execution of native methods is impossible.");
		}

		// Save current frame...
		frame.setPc(frame.getVm().getPc() + 1 + this.getNumberOfOtherBytes());
		frame.getVm().getStack().push(frame);

		// Push new one.
		frame.getVm().createAndPushFrame(frame, method, parameters);

		// Finish.
		frame.getVm().setReturnFromCurrentExecution(true);
	}

	/**
	 * Do the checks for static/non-static methods calls and get the {@link ClassFile} of the object
	 * reference to invoke the method on for non-static ones. For some instructions it has to be
	 * neither the instance initializer nor the static initializer, or at least not the instance
	 * initializer. For invokestatic is has to be static.
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
	protected abstract ClassFile checkStaticMethod(Frame frame, String[] nameAndType,
			Method method, Object[] parameters) throws ExecutionException, VmRuntimeException;

	/**
	 * Perform access checks if required by the instruction.
	 *
	 * @param frame The currently executed frame.
	 * @param method The resolved method.
	 * @param objectrefClassFile The {@link ClassFile} of the object reference to invoke the method on.
	 * @throws ClassFileException If a required class file cannot be loaded.
	 * @throws VmRuntimeException If an unexpected condition it met and a runtime exception is
	 *         thrown.
	 */
	protected abstract void checkAccess(Frame frame, Method method, ClassFile objectrefClassFile)
			throws ClassFileException, VmRuntimeException;
	
	/**
	 * Check if the method is neither an instance initialization method, nor the class or interface initialization
	 * method
	 * 
	 * @param method
	 * @throws ExecutionException
	 */
	protected void checkNoInstanceInit(Method method) throws ExecutionException {
		// The method must be neither the instance initializer nor the static initializer.
		if (method.getName().equals(VmSymbols.OBJECT_INITIALIZER_NAME))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The Method must not be the instance initialization method.");
		if (method.getName().equals(VmSymbols.CLASS_INITIALIZER_NAME))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The Method must not be the class or interface initialization method.");
	}

	/**
	 * If required by the instruction, select the actual method for invocation and perform final
	 * checks on it.
	 * 
	 * Example: objectrefClassFile is type HashMap
	 * Method is put
	 * methodClassFile is map ("put" is abstract in there)
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
	protected abstract Method selectMethod(Frame frame, Method method, ClassFile methodClassFile,
			ClassFile objectrefClassFile) throws ClassFileException, VmRuntimeException;

	/**
	 * Check the return type of a native method and pushes a value onto the operand stack of
	 * the executing frame. This value will be null for reference types and the applying
	 * representation of zero for primitive types and their java.lang wrapper classes.<br />
	 * <br />
	 * Only native methods will be processed. This method is only meant to be used if
	 * native methods are to be skipped AND their return values should be resumed to be
	 * zero / null values.
	 *
	 * @param stack The operand stack of the current frame.
	 * @param method The native method.
	 * @param symbolic Toggles whether a symbolic or a normal type is returned.
	 */
	public static void pushZeroOrNull(Stack<Object> stack, Method method, boolean symbolic) {
		if (method.isAccNative()) {
			String type = method.getReturnType();
			// Only process if the return type is not void.
			if (!type.equals("void")) {
				if (symbolic) {
					if (type.equals("double") || type.equals("java.lang.Doble")) {
						stack.push(DoubleConstant.getInstance(0D));
					} else if (type.equals("float") || type.equals("java.lang.Float")) {
						stack.push(FloatConstant.getInstance(0F));
					} else if (type.equals("int") || type.equals("java.lang.Integer")
							|| type.equals("byte") || type.equals("java.lang.Byte")
							|| type.equals("char") || type.equals("java.lang.Char")
							|| type.equals("short") || type.equals("java.lang.Short")
							|| type.equals("boolean") || type.equals("java.lang.Boolean")) {
						stack.push(IntConstant.getInstance(0));
					} else if (type.equals("long") || type.equals("java.lang.Long")) {
						stack.push(LongConstant.getInstance(0L));
					} else {
						stack.push(null);
					}
				} else {
					if (type.equals("double") || type.equals("java.lang.Doble")) {
						stack.push(Double.valueOf(0D));
					} else if (type.equals("float") || type.equals("java.lang.Float")) {
						stack.push(Float.valueOf(0F));
					} else if (type.equals("int") || type.equals("java.lang.Integer")
							|| type.equals("byte") || type.equals("java.lang.Byte")
							|| type.equals("char") || type.equals("java.lang.Char")
							|| type.equals("short") || type.equals("java.lang.Short")
							|| type.equals("boolean") || type.equals("java.lang.Boolean")) {
						stack.push(Integer.valueOf(0));
					} else if (type.equals("long") || type.equals("java.lang.Long")) {
						stack.push(Long.valueOf(0L));
					} else {
						stack.push(null);
					}
				}
			}
		}
	}

	/**
	 * Get the number of other bytes for this instruction.
	 *
	 * @return The number of other bytes.
	 */
	@Override
	public int getNumberOfOtherBytes() {
		return 2;
	}

	/**
	 * Get the method to execute.<br />
	 * <br />
	 * This method is supposed to be used for static analysis and not by any runtime components!
	 *
	 * @param constantPool The constant pool of the class of the method of currently executed frame.
	 * @param classLoader An appropriately initialized class loader.
	 * @return The method to execute.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException In case of any other problems.
	 */
	protected Method getMethod(Constant[] constantPool, MugglClassLoader classLoader)
			throws ClassFileException, ExecutionException {
		int index = this.otherBytes[0] << ONE_BYTE | this.otherBytes[1];
		Constant constant = constantPool[index];

		// Get the name and the descriptor.
		String[] nameAndType = getNameAndType(constant);
		ClassFile methodClassFile = getMethodClassFile(constant, classLoader);

		// Try to resolve method from this class.
		ResolutionAlgorithms resoluton = new ResolutionAlgorithms(classLoader);
		Method method;
		try {
			if (methodClassFile.isAccInterface())
				method = resoluton.resolveMethodInterface(methodClassFile, nameAndType);
			else
				method = resoluton.resolveMethod(methodClassFile, nameAndType);
		} catch (ClassFileException e) {
			throw new ExecutionException(e);
		} catch (NoSuchMethodError e) {
			throw new ExecutionException(e);
		}

		return method;
	}

	/**
	 * Get the number of arguments that the method to invoked expects.<br />
	 * <br />
	 * This method is supposed to be used for static analysis and not by any runtime components!
	 *
	 * @param constantPool The constant pool of the class of the method of currently executed frame.
	 * @param classLoader An appropriately initialized class loader.
	 * @return The number of arguments that the method to invoked expects.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException In case of any other problems.
	 */
	public int getNumberOfArguments(Constant[] constantPool, MugglClassLoader classLoader)
			throws ClassFileException, ExecutionException {
		return getMethod(constantPool, classLoader).getNumberOfArguments();
	}

	/**
	 * Get the types of elements this instruction will pop from the stack.<br />
	 * <br />
	 * This method is supposed to be used for static analysis and not by any runtime components!
	 * 
	 * @param methodClassFile The class file of the method this instruction belongs to.
	 * @return The types this instruction pops. The length of the arrays reflects the number of
	 *         elements pushed in the order they are pushed. Types are {@link ClassFile#T_BOOLEAN},
	 *         {@link ClassFile#T_BYTE} {@link ClassFile#T_CHAR}, {@link ClassFile#T_DOUBLE},
	 *         {@link ClassFile#T_FLOAT}, {@link ClassFile#T_INT}, {@link ClassFile#T_LONG} and
	 *         {@link ClassFile#T_SHORT}, 0 to indicate a reference or return address type or -1 to
	 *         indicate the popped type cannot be determined statically.
	 */
	public byte[] getTypesPopped(ClassFile methodClassFile) {
		byte[] types;
		Method method;
		try {
			method = getMethod(methodClassFile.getConstantPool(), methodClassFile.getClassLoader());

			String[] typeStrings = method.getParameterTypesAsArray();
			types = new byte[typeStrings.length];
			for (int a = 0; a < typeStrings.length; a++) {
				if (typeStrings[a].equals("boolean")) {
					types[a] = ClassFile.T_BOOLEAN;
				} else if (typeStrings[a].equals("byte")) {
					types[a] = ClassFile.T_BYTE;
				} else if (typeStrings[a].equals("char")) {
					types[a] = ClassFile.T_CHAR;
				} else if (typeStrings[a].equals("double")) {
					types[a] = ClassFile.T_DOUBLE;
				} else if (typeStrings[a].equals("float")) {
					types[a] = ClassFile.T_FLOAT;
				} else if (typeStrings[a].equals("int")) {
					types[a] = ClassFile.T_INT;
				} else if (typeStrings[a].equals("long")) {
					types[a] = ClassFile.T_LONG;
				} else if (typeStrings[a].equals("short")) {
					types[a] = ClassFile.T_SHORT;
				}
			}
		} catch (ClassFileException e) {
			types = new byte[1];
			types[0] = -1;
		} catch (ExecutionException e) {
			types = new byte[1];
			types[0] = -1;
		}

		return types;
	}
	
	/**
	 * Get name and descriptor from a constant_methodref.
	 *
	 * @param constant A constant_methodref.
	 * @return Name and descriptor for the constant_methodref.
	 * @throws ExecutionException In case of any other problems.
	 */
	protected String[] getNameAndType(Constant constant) throws ExecutionException {
		if (!(constant instanceof ConstantMethodref)) {
			throw new ExecutionException(
					"Error while executing instruction " + getName()
						+ ": Expected runtime constant pool item at index "
						+ "to be a symbolic reference to a method.");
		}
		
		return ((ConstantMethodref) constant).getNameAndTypeInfo();
	}
	
	/**
	 * Get the corresponding class file for a constant_methodref.
	 *
	 * @param constant A constant_methodref.
	 * @param classLoader The class loader.
	 * @return The corresponding class file for a constant_methodref.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException In case of any other problems.
	 */
	protected ClassFile getMethodClassFile(Constant constant, MugglClassLoader classLoader) throws ClassFileException, ExecutionException {
		if (!(constant instanceof ConstantMethodref)) {
			throw new ExecutionException(
					"Error while executing instruction " + getName()
						+ ": Expected runtime constant pool item at index "
						+ "to be a symbolic reference to a method.");
		}
		
		return classLoader.getClassAsClassFileOrArrays(
				((ConstantMethodref) constant).getClassName());
	}

}
