package de.wwu.muggl.instructions.bytecode;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.general.Get;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.exceptions.ExceptionHandler;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.exceptions.SymbolicExceptionHandler;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * Implementation of the instruction <code>getfield</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-10
 */
public class Getfield extends Get implements Instruction {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method that the
	 * instruction belongs to is supplied as an argument.
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized successfully, most likely due to missing additional bytes. This might be caused by a corrupt classfile, or a classfile of a more recent version than what can be handled.
	 */
	public Getfield(AttributeCode code) throws InvalidInstructionInitialisationException {
		super(code);
	}

	/**
	 * Execute the instruction.
	 * @param frame The currently executed frame.
	 * @throws ExecutionException Thrown in case of fatal problems during the execution.
	 */
	@Override
	public void execute(Frame frame) throws ExecutionException {
		try {
			// Fetch and push the value.
			frame.getOperandStack().push(getFieldValue(frame));
		} catch (VmRuntimeException e) {
			ExceptionHandler handler = new ExceptionHandler(frame, e);
			try {
				handler.handleException();
			} catch (ExecutionException e2) {
				executionFailed(e2);
			}
		} catch (ExecutionException e) {
			executionFailed(e);
		}
	}

	/**
	 * Execute the instruction symbolically.
	 * @param frame The currently executed frame.
	 * @throws NoExceptionHandlerFoundException If no handler could be found.
	 * @throws SymbolicExecutionException Thrown in case of fatal problems during the symbolic execution.
	 */
	@Override
	public void executeSymbolically(Frame frame) throws NoExceptionHandlerFoundException, SymbolicExecutionException {
		try {
			// Get the fields' value.
			Object value = getFieldValue(frame);

			// Push it.
			frame.getOperandStack().push(value);
		} catch (VmRuntimeException e) {
			SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, e);
			try {
				handler.handleException();
			} catch (ExecutionException e2) {
				executionFailedSymbolically(e2);
			}
		} catch (SymbolicExecutionException e) {
			executionFailedSymbolically(e);
		} catch (ExecutionException e) {
			executionFailedSymbolically(e);
		}
	}

	/**
	 * Get the value of an instance field.
	 * @param frame The currently executed frame.
	 * @return The fields' value.
	 * @throws ExecutionException In case of fatal problems during the execution.
	 * @throws VmRuntimeException If the Field could not be found, wrapping a NoSuchFieldError.
	 */
	private Object getFieldValue(Frame frame) throws ExecutionException, VmRuntimeException {
		// Preparations.
		Objectref objectref = (Objectref) frame.getOperandStack().pop();
		ClassFile methodClassFile = frame.getMethod().getClassFile();
		Field field = getField(frame, methodClassFile, methodClassFile.getClassLoader());

		// Runtime exception: is the field static?
		if (field.isAccStatic())
			throw new VmRuntimeException(frame.getVm().generateExc(
					"java.lang.IncompatibleClassChangeError",
					"The field accessed by instruction " + getName() + " must not be static."));

		// Runtime exception: objectref is null.
		if (objectref == null)
			throw new VmRuntimeException(frame.getVm()
					.generateExc("java.lang.NullPointerException"));

		// Fetch the class of objectref.
		ClassFile objectrefClassFile = objectref.getInitializedClass().getClassFile();
		Class<?> objectrefClass = objectrefClassFile.getClass();

		// Unexpected exception: objectref is an array.
		if (objectrefClass.isArray()) throw new ExecutionException("Objectref must not be an array.");

		// Is the access allowed?
		if (field.isAccProtected()) {
			// Runtime exception: Due to the kind of resolution the Field has to be a member of the current class or of one of its superclasses. So check objectref.
			if (!field.getClassFile().getName().equals(objectrefClassFile.getName())) {
				// Objectref is not the same class. Is it a subclass then.
				boolean classMatchFound = false;
				while (objectrefClassFile.getSuperClass() != 0) {
					try {
						objectrefClassFile = frame.getVm().getClassLoader().getClassAsClassFile(objectrefClassFile.getConstantPool()[objectrefClassFile.getSuperClass()].getStringValue());
					} catch (ClassFileException e) {
						throw new VmRuntimeException(frame.getVm().generateExc(
								"java.lang.NoClassDefFoundError", e.getMessage()));
					}

					if (field.getClassFile().getName().equals(objectrefClassFile.getName())) {
						// Found the match!
						classMatchFound = true;
						break;
					}
				}
				if (!classMatchFound)
					throw new VmRuntimeException(frame.getVm().generateExc(
							"java.lang.IllegalAccessError",
							objectref.getInitializedClass().getClassFile().getName()
									+ " may not access field " + field.getName() + " in class "
									+ frame.getMethod().getClassFile().getName() + "."));
			}
		}

		// Get the value of the field.
		Object value = objectref.getField(field);

		// Return the value.
		return value;
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "getfield";
	}

	/**
	 * Get the thrown exception types as fully qualified java names.
	 * @return The thrown exception types.
	 */
	public String[] getThrownExceptionTypes() {
		String[] exceptionTypes = { "java.lang.IllegalAccessError",
									"java.lang.NoClassDefFoundError",
									"java.lang.NoSuchFieldError",
									"java.lang.NullPointerException"};
		return exceptionTypes;
	}

}
