package de.wwu.muggl.instructions.bytecode;

import java.util.Stack;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.general.CheckcastInstanceof;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpException;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantClass;
import de.wwu.muggl.vm.exceptions.ExceptionHandler;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionAlgorithms;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.exceptions.SymbolicExceptionHandler;
import de.wwu.muggl.vm.initialization.ReferenceValue;

/**
 * Implementation of the instruction  <code>checkcast</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-10
 */
public class Checkcast extends CheckcastInstanceof implements Instruction, JumpException {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method that the
	 * instruction belongs to is supplied as an argument.
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized successfully, most likely due to missing additional bytes. This might be caused by a corrupt classfile, or a classfile of a more recent version than what can be handled.
	 */
	public Checkcast(AttributeCode code) throws InvalidInstructionInitialisationException {
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
			Stack<Object> stack = frame.getOperandStack();
			// there might be primitive types on the stack, such as java.lang.integer and not only ReferenceValues
			// the contract and implementation with checkForAssignmentCompatibility says that you can pass an object, takes care.
			Object objectref = stack.peek(); // Peek - the operand stack stays unchanged (or is discarded completely in case of a ClassCastException).
			Object constant = frame.getConstantPool()[this.otherBytes[0] << ONE_BYTE | this.otherBytes[1]];

			// Unexpected exception: the element from the constant_pool is no ConstantClass.
			if (!(constant instanceof ConstantClass))
				throw new ExecutionException("The constant_pool entry fetched does not have the correct type.");

			// objectref must be either null or is assignment compatible (can be cast to) the expected class.
			String castingToClassName = ((ConstantClass) constant).getName();
			ExecutionAlgorithms ea = new ExecutionAlgorithms(frame.getVm().getClassLoader());
			if (objectref != null && !ea.checkForAssignmentCompatibility(objectref, castingToClassName, frame.getVm(), false)) {
				// Checking of cast failed. Throw a ClassCastException exception.
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ClassCastException", ((ReferenceValue) objectref).getInitializedClass()
						.getClassFile().getName()
						+ " cannot be cast to " + castingToClassName));
			}
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
	 *
	 * @param frame The currently executed frame.
	 * @throws NoExceptionHandlerFoundException If no handler could be found.
	 * @throws SymbolicExecutionException Thrown in case of fatal problems during the symbolic execution.
	 */
	@Override
	public void executeSymbolically(Frame frame) throws NoExceptionHandlerFoundException, SymbolicExecutionException {
		try {
			Stack<Object> stack = frame.getOperandStack();
			ReferenceValue objectref = (ReferenceValue) stack.peek(); // Peek - the operand stack stays unchanged (or is discarded completely in case of a ClassCastException).
			Object constant = frame.getConstantPool()[this.otherBytes[0] << ONE_BYTE | this.otherBytes[1]];

			// Unexpected exception: the element from the constant_pool is no ConstantClass.
			if (!(constant instanceof ConstantClass))
				throw new SymbolicExecutionException("The constant_pool entry fetched does not have the correct type.");

			// objectref must be either null or is assignment compatible (can be cast to) the expected class.
			String castingToClassName = ((ConstantClass) constant).getName();
			ExecutionAlgorithms ea = new ExecutionAlgorithms(frame.getVm().getClassLoader());
			if (objectref != null && !ea.checkForAssignmentCompatibility(objectref, castingToClassName, frame.getVm(), false)) {
				// Checking of cast failed. Throw a ClassCastException exception.
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ClassCastException", objectref.getInitializedClass()
						.getClassFile().getName()
						+ " cannot be cast to " + castingToClassName));
			}
		} catch (VmRuntimeException e) {
			SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, e);
			try {
				handler.handleException();
			} catch (ExecutionException e2) {
				executionFailedSymbolically(e2);
			}
		} catch (ExecutionException e) {
			executionFailedSymbolically(e);
		}
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "checkcast";
	}

	/**
	 * Get the thrown exception types as fully qualified java names.
	 * @return The thrown exception types.
	 */
	public String[] getThrownExceptionTypes() {
		String[] exceptionTypes = { "java.lang.ClassCastException",
									"java.lang.IllegalAccessError",
									"java.lang.NoClassDefFoundError"};
		return exceptionTypes;
	}
}
