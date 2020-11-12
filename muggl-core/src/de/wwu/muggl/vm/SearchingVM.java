package de.wwu.muggl.vm;

import de.wwu.muggl.instructions.general.GeneralInstructionWithOtherBytes;
import de.wwu.muggl.instructions.general.Switch;
import de.wwu.muggl.search.SearchAlgorithm;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.*;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.initialization.*;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.searchtree.Choice;

import java.util.Stack;

/** 
 * Common interface for all virtual machines that support logic variables and searching (Symbolic, Logic)
 * @author Jan C. Dageförde
 */
public abstract class SearchingVM extends VirtualMachine {

	public SearchingVM(
			Application application,
			MugglClassLoader mugglClassLoader,
			ClassFile classFile,
			de.wwu.muggl.vm.classfile.structures.Method method) throws InitializationException {
		super(application, mugglClassLoader, classFile, method);
	}

	/**
	 * Getter for the SolverManager.
	 * 
	 * @return The SolverManager of this VirtualMachine.
	 */
	public abstract SolverManager getSolverManager();
	
	/**
	 * Getter for the search algorithm implemented in this symbolic virtual machine.
	 * 
	 * @return The SearchAlgorithm.
	 */
	public abstract SearchAlgorithm getSearchAlgorithm();

	/**
	 * Generate a new choice point.
	 * 
	 * @param instruction The instruction that wants to generate the choice points.
	 * @param constraintExpression The ConstraintExpression describing the choice a if it is
	 *        conditional jump Instruction. May be null.

	 * @throws SymbolicExecutionException If the instruction supplied is no conditional jump, no
	 *         load instruction or if an Exception is thrown during the choice point generation.
	 */
	public abstract void generateNewChoicePoint(GeneralInstructionWithOtherBytes instruction,
			ConstraintExpression constraintExpression)
			throws SymbolicExecutionException;

    public abstract void generateNewChoicePoint(Switch instruction, Term termFromStack, IntConstant[] keys,
                                       int[] pcs, IntConstant low, IntConstant high) throws ExecutionException;

	public abstract void increaseTimeChoicePointGeneration(long increment);

	public abstract void increaseTimeSolvingForChoicePointGeneration(long increment);

    public abstract Choice getCurrentChoice();

    /**
     * Obtain current trail and reset it for further execution
     * @return Trail stack
     */
    public abstract Stack<TrailElement> extractCurrentTrail();

    public abstract void addToTrail(TrailElement element);

    public abstract boolean isInSearch();

    public abstract void saveFieldValue(FieldPut fieldValue);

    public abstract void saveLocalVariableValue(Restore valueRepresentation);

	public abstract void saveArrayValue(ArrayRestore valueRepresentation);

	public abstract void storeRepresentationForFreeVariable(Frame frame, int freeVariableIndex);

	public abstract FreeObjectref getAFreeObjectref(ClassFile classFile);

	public abstract Objectref getAPrimitiveWrapperObjectref(ClassFile classFile) throws PrimitiveWrappingImpossibleException;
}
