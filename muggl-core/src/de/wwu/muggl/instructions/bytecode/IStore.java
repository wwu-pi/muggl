package de.wwu.muggl.instructions.bytecode;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.general.Store;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.typed.IntegerInstruction;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;

import java.util.Arrays;

/**
 * Implementation of the instruction <code>istore</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2009-03-26
 */
public class IStore extends Store implements Instruction {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method that the
	 * instruction belongs to is supplied as an argument, as well as the index of the instruction, pointing at
	 * the local variable that it is responsible for. Also initialize the TypedInstruction.
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @param index The index to load from. Must be a value between 0 and 3 inclusively. Otherwise, the index is set to 255 and methods are required to calculate if from the additional bytes.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized successfully, most likely due to missing additional bytes. This might be caused by a corrupt classfile, or a classfile of a more recent version than what can be handled.
	 */
	public IStore(AttributeCode code, int index) throws InvalidInstructionInitialisationException {
		super(code, index);
		this.typedInstruction = new IntegerInstruction();
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "i" + super.getName();
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
		byte[] types = {ClassFile.T_INT};
		return types;
	}

	@Override
	public String toString() {
		return "{IStore " + getName() + " :: " + index + " " + Arrays.toString(otherBytes) + "}";
	}
}
