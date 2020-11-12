package de.wwu.muggl.instructions.general;

import java.util.Stack;

import de.wwu.muggl.instructions.interfaces.control.JumpException;
import de.wwu.muggl.instructions.interfaces.data.StackPop;
import de.wwu.muggl.instructions.interfaces.data.StackPush;
import de.wwu.muggl.instructions.typed.TypedInstruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.exceptions.ExceptionHandler;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.exceptions.SymbolicExceptionHandler;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.solvers.expressions.IntConstant;

/**
 * Abstract instruction with some concrete methods for loading elements from an array.
 * Concrete instructions can be extended from this class.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-11
 */
public abstract class Aload extends GeneralInstruction implements JumpException, StackPop, StackPush {
	/**
	 * The type the inheriting class will take.
	 */
	protected TypedInstruction typedInstruction;

	/**
	 * Execute the inheriting instruction.
	 * @param frame The currently executed frame.
	 * @throws ExecutionException Thrown in case of fatal problems during the execution.
	 */
	@Override
	public void execute(Frame frame) throws ExecutionException {
		try { /// TODO To change for free arrays?
			// Preparations.
			Stack<Object> stack = frame.getOperandStack();

			Object index = stack.pop();
			int idx;
			if (index instanceof Character) {
			    idx = (int)(((Character) index).charValue()); // TODO why does this happen sometimes?!
            } else {
			    idx = (int)index;
            }
			Object arrayrefObject = stack.pop();

			// Runtime exception: arrayref is null
			if (arrayrefObject == null)
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));

			// Unexpected exception: Arrayref does not point to an array.
			if (!(arrayrefObject instanceof Arrayref)) {
				throw new ExecutionException("Could not " + getName() + " array entry #" + index + ": Expected an array, but did not get one.");
			}
			Arrayref arrayref = (Arrayref) arrayrefObject;

			// Runtime exception array index out of bounds.
			if (idx >= arrayref.getLength() || idx < 0) {
				throw new VmRuntimeException(frame.getVm().generateExc(
						"java.lang.ArrayIndexOutOfBoundsException", "Array index is out of bounds"));
			}

			// Unexpected exception: The object at the index is not of one of the required types.
			Object value = arrayref.getElement(idx);
			value = this.typedInstruction.validateAndExtendValue(value);

			// Push the value.
			stack.push(value);
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
	 * @throws SymbolicExecutionException Thrown in case of fatal problems during the symbolic
	 *         execution.
	 */
	@Override
	public void executeSymbolically(Frame frame) throws NoExceptionHandlerFoundException,
			SymbolicExecutionException {
		try { /// TODO To change for free arrays?
			// Preparations.
			Stack<Object> stack = frame.getOperandStack();
            Object elem = stack.pop();
            int index;
            if (elem instanceof IntConstant) {
                index = ((IntConstant) elem).getValue();
            } else {
                index = (Integer)elem;
            }
			Object arrayrefObject  = stack.pop();

			// Runtime exception: arrayref is null
			if (arrayrefObject == null)
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));

			// Unexpected exception: arrayref does not point to an array.
			if (!(arrayrefObject instanceof Arrayref)) {
				throw new SymbolicExecutionException("Could not " + getName() + " array entry #" + index + ": Expected an array, but did not get one.");
			}
			Arrayref arrayref = (Arrayref) arrayrefObject;

			// Runtime exception array index out of bounds.
			if (arrayref.getLength() <= index || index < 0) {
				throw new VmRuntimeException(frame.getVm().generateExc(
						"java.lang.ArrayIndexOutOfBoundsException", "Array index is out of bounds"));
			}

			Object value = arrayref.getElement(index);

			// Push the value.
			stack.push(value);
		} catch (VmRuntimeException e) {
			SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, e);
			try {
				handler.handleException();
			} catch (ExecutionException e2) {
				executionFailedSymbolically(e2);
			}
		} catch (SymbolicExecutionException e) {
			executionFailedSymbolically(e);
		}
	}

	/**
	 * Get the number of other bytes for this instruction.
	 * @return The number of other bytes.
	 */
	@Override
	public int getNumberOfOtherBytes() {
		return 0;
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "aload";
	}

	/**
	 * Get the thrown exception types as fully qualified java names.
	 * @return The thrown exception types.
	 */
	public String[] getThrownExceptionTypes() {
		String[] exceptionTypes = { "java.lang.ArrayIndexOutOfBoundsException",
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
	 * Get the number of elements that will be pushed onto the stack when this instruction is
	 * executed.
	 *
	 * @return The number of elements that will be pushed onto the stack.
	 */
	public int getNumberOfPushedElements() {
		return 1;
	}

	/**
	 * Get the types of elements this instruction will pop from the stack.
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
		byte[] types = {ClassFile.T_INT, 0};
		return types;
	}

}
