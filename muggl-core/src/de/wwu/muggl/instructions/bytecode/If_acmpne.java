package de.wwu.muggl.instructions.bytecode;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.general.If_acmp;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;

/**
 * Implementation of the instruction <code>if_acmpne</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2008-11-04
 */
public class If_acmpne extends If_acmp implements Instruction {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method that the
	 * instruction belongs to is supplied as an argument. Also the line number is given as an argument, since
	 * it is needed in case of jumping.
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @param lineNumber The line number of this instruction (including other bytes!).
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized successfully, most likely due to missing additional bytes. This might be caused by a corrupt classfile, or a classfile of a more recent version than what can be handled.
	 */
	public If_acmpne(AttributeCode code, int lineNumber) throws InvalidInstructionInitialisationException {
		super(code, lineNumber);
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "if_acmpne";
	}

	/**
	 * Compare two reference values. Return true if the expected condition is met, false otherwise.
	 *
	 * @param value1 The first reference value.
	 * @param value2 The second reference value.
	 * @return true If the expected condition is met, false otherwise.
	 */
	@Override
	protected boolean compare(ReferenceValue value1, ReferenceValue value2) {
		if (value1 instanceof Objectref && value2 instanceof Objectref) {
			// impossible to have the same object representing the <array>.getclass, so fake here
			Objectref o1 = (Objectref) value1;
			Objectref o2 = (Objectref) value2;
			if (o1.isMirroredMugglIsArray() && o2.isMirroredMugglIsArray()) {
				return o1.getMirroredMugglArray().getReferenceValue().getInitializedClass().getClassFile() != o2
						.getMirroredMugglArray().getReferenceValue().getInitializedClass().getClassFile();
			}
		}
		return value1 != value2;
	}

}
