package de.wwu.muggl.instructions.bytecode;

import java.util.Stack;

import de.wwu.muggl.instructions.general.Astore;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.typed.ReferenceInstruction;
import de.wwu.muggl.solvers.expressions.BooleanConstant;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.ArrayRestore;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.exceptions.ExceptionHandler;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.impl.symbolic.exceptions.SymbolicExceptionHandler;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.initialization.FreeObjectref;

/**
 * Implementation of the instruction  <code>aastore</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-10
 */
public class AAstore extends Astore implements Instruction {

	/**
	 * Constructor to initialize the TypedInstruction.
	 */
	public AAstore() {
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
		byte[] types = {0, ClassFile.T_INT, 0};
		return types;
	}

}
