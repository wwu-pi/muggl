package de.wwu.muggl.instructions.bytecode;

import java.util.Stack;

import de.wwu.muggl.instructions.general.Aload;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.typed.ReferenceInstruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.exceptions.ExceptionHandler;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.initialization.Arrayref;

/**
 * Implementation of the instruction <code>aaload</code>. It loads a reference from an array.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-10
 */
public class AAload extends Aload implements Instruction {

	/**
	 * Constructor to initialize the TypedInstruction.
	 */
	public AAload() {
		 this.typedInstruction = new ReferenceInstruction();
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "a" + super.getName();
	}

	/**
	 * Get the type of elements this instruction will push onto the stack.
	 *
	 * @param methodClassFile The class file of the method this instruction belongs to.
	 * @return The type this instruction pushes. Types are {@link ClassFile#T_BOOLEAN},
	 *         {@link ClassFile#T_BYTE} {@link ClassFile#T_CHAR}, {@link ClassFile#T_DOUBLE},
	 *         {@link ClassFile#T_FLOAT}, {@link ClassFile#T_INT}, {@link ClassFile#T_LONG} and
	 *         {@link ClassFile#T_SHORT}, 0 to indicate an reference or return address type or -1 to
	 *         indicate the pushed type cannot be determined statically.
	 */
	public byte getTypePushed(ClassFile methodClassFile) {
		return 0;
	}

}
