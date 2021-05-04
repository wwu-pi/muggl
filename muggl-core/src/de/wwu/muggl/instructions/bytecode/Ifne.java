package de.wwu.muggl.instructions.bytecode;

import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.general.If;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.ST;

import java.util.Optional;
import java.util.Stack;

/**
 * Implementation of the instruction <code>ifne</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2008-02-25
 */
public class Ifne extends If implements Instruction {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method that the
	 * instruction belongs to is supplied as an argument. Also the line number is given as an argument, since
	 * it is needed in case of jumping.
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @param lineNumber The line number of this instruction (including other bytes!).
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized successfully, most likely due to missing additional bytes. This might be caused by a corrupt classfile, or a classfile of a more recent version than what can be handled.
	 */
	public Ifne(AttributeCode code, int lineNumber) throws InvalidInstructionInitialisationException {
		super(code, lineNumber);
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "ifne";
	}

	/**
	 * Compare the value to 0. Return true if it is not equal to it.
	 * @param value The value to compare to 0.
	 * @return true if the expected condition is met, false otherwise.
	 */
	@Override
	protected boolean compare(int value) {
		if (value != 0) return true;
		return false;
	}

	/**
	 * Get the ConstraintExpression for this instruction.
	 * @param term1 The left-hand term.
	 * @param term2 The right-hand term.
	 * @return A new ConstraintExpression.
	 */
	@Override
	protected ConstraintExpression getConstraintExpression(Term term1, Term term2) {
		return NumericNotEqual.newInstance(term1, term2);
	}

	@Override
	public Optional<ST> executeMuli(SearchingVM vm, Frame frame) {
		if (!vm.isInSearch()) {
			super.execute(frame);
			return Optional.empty();
		}

		Stack<Object> stack = frame.getOperandStack();
		Object op = stack.pop();
		if (op instanceof BooleanVariable) {
			return Optional.of(new Choice(frame, getPcOfSubsequentInstruction(vm),
					getJumpTarget(), ((BooleanVariable) op) , vm.extractCurrentTrail(), vm.getCurrentChoice()));
		} else if (op instanceof NumericVariable) {
			return Optional.of(new Choice(frame,
					getPcOfSubsequentInstruction(vm),
					getJumpTarget(),
					Not.newInstance(NumericEqual.newInstance(
							(NumericVariable) op,
							IntConstant.getInstance(0))),
					vm.extractCurrentTrail(),
					vm.getCurrentChoice())
			);
		} else {
			stack.push(op);
			super.execute(frame);
			return Optional.empty();
		}
	}
}
