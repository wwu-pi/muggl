package de.wwu.muggl.instructions.bytecode;

import java.util.Stack;

import de.wwu.muggl.instructions.general.Div;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.execution.ExecutionException;

/**
 * Implementation of the instruction <code>fdiv</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-10
 */
public class FDiv extends Div implements Instruction {

	/**
	 * Execute the instruction.
	 * @param frame The currently executed frame.
	 * @throws ExecutionException Thrown in case of fatal problems during the execution.
	 */
	@Override
	@SuppressWarnings("unused")
	public void execute(Frame frame) throws ExecutionException {
		Stack<Object> stack = frame.getOperandStack();
		Float value2 = (Float) stack.pop();
		Float value1 = (Float) stack.pop();
		stack.push(value1 / value2);
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "fdiv";
	}

	/**
	 * Get the type of elements this instruction will push onto the stack.
	 *
	 * @param methodClassFile The class file of the method this instruction belongs to.
	 * @return The type this instruction pushes. Types are {@link ClassFile#T_BOOLEAN},
	 *         {@link ClassFile#T_BYTE} {@link ClassFile#T_CHAR}, {@link ClassFile#T_DOUBLE},
	 *         {@link ClassFile#T_FLOAT}, {@link ClassFile#T_INT}, {@link ClassFile#T_LONG} and
	 *         {@link ClassFile#T_SHORT}, 0 to indicate a reference or return address type or -1 to
	 *         indicate the pushed type cannot be determined statically.
	 */
	public byte getTypePushed(ClassFile methodClassFile) {
		return ClassFile.T_FLOAT;
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
		byte[] types = {ClassFile.T_FLOAT, ClassFile.T_FLOAT};
		return types;
	}

}