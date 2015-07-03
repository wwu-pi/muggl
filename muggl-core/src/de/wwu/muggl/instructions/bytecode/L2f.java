package de.wwu.muggl.instructions.bytecode;

import java.util.Stack;

import de.wwu.muggl.instructions.general.Conversion;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.testtool.expressions.Expression;
import de.wwu.testtool.expressions.Term;
import de.wwu.testtool.expressions.TypeCast;

/**
 * Implementation of the instruction <code>l2f</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2009-03-26
 */
public class L2f extends Conversion implements Instruction {

	/**
	 * Execute the instruction.
	 * @param frame The currently executed frame.
	 */
	@Override
	public void execute(Frame frame) {
		Stack<Object> stack = frame.getOperandStack();
		stack.push(new Float(((Long) stack.pop()).floatValue()));
	}

	/**
	 * Execute the instruction symbolically.
	 * @param frame The currently executed frame.
	 */
	@Override
	public void executeSymbolically(Frame frame) {
		Stack<Object> stack = frame.getOperandStack();
		Term oldTerm = (Term) stack.pop();
		Term newTerm = TypeCast.newInstance(oldTerm, oldTerm.getType(), Expression.FLOAT);
		stack.push(newTerm);
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "l2f";
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
		byte[] types = {ClassFile.T_LONG};
		return types;
	}

}