package de.wwu.muggl.instructions.bytecode;

import java.util.Stack;

import de.wwu.muggl.instructions.general.Xor;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;

/**
 * Implementation of the instruction <code>ixor</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2009-03-26
 */
public class IXor extends Xor implements Instruction {

	/**
	 * Execute the instruction.
	 *
	 * @param frame The currently executed frame.
	 */
	@Override
	public void execute(Frame frame) {
		Stack<Object> stack = frame.getOperandStack();
		Object value2 = stack.pop();
		Object value1 = stack.pop();
		int val1 = value1 instanceof IntConstant ? ((IntConstant) value1).getValue() : (Integer) value1;
		int val2 = value2 instanceof IntConstant ? ((IntConstant) value2).getValue() : (Integer) value2;
		stack.push(val2 ^ val1);
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "ior";
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
		return ClassFile.T_INT;
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
		byte[] types = {ClassFile.T_INT, ClassFile.T_INT};
		return types;
	}

}
