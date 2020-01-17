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
import de.wwu.muggl.vm.initialization.FreeObjectref;
import de.wwu.muli.searchtree.Choice;

import java.util.Stack;

/** 
 * Common interface for all virtual machines that support logic variables and searching (Symbolic, Logic)
 * @author Jan C. Dageförde
 */
public interface SearchingVM {
	
	/**
	 * Getter for the SolverManager.
	 * 
	 * @return The SolverManager of this VirtualMachine.
	 */
	public SolverManager getSolverManager();
	
	/**
	 * Getter for the search algorithm implemented in this symbolic virtual machine.
	 * 
	 * @return The SearchAlgorithm.
	 */
	public SearchAlgorithm getSearchAlgorithm();

	/**
	 * Generate a new choice point.
	 * 
	 * @param instruction The instruction that wants to generate the choice points.
	 * @param constraintExpression The ConstraintExpression describing the choice a if it is
	 *        conditional jump Instruction. May be null.

	 * @throws SymbolicExecutionException If the instruction supplied is no conditional jump, no
	 *         load instruction or if an Exception is thrown during the choice point generation.
	 */
	public void generateNewChoicePoint(GeneralInstructionWithOtherBytes instruction,
			ConstraintExpression constraintExpression)
			throws SymbolicExecutionException;

    public void generateNewChoicePoint(Switch instruction, Term termFromStack, IntConstant[] keys,
                                       int[] pcs, IntConstant low, IntConstant high) throws ExecutionException;

	void increaseTimeChoicePointGeneration(long increment);

	void increaseTimeSolvingForChoicePointGeneration(long increment);

    int getPc();

    void setPC(int jumpTarget);

    Choice getCurrentChoice();

    /**
     * Obtain current trail and reset it for further execution
     * @return Trail stack
     */
    Stack<TrailElement> extractCurrentTrail();

    void addToTrail(TrailElement element);

    boolean isInSearch();

    void saveFieldValue(FieldPut fieldValue);

    void saveLocalVariableValue(Restore valueRepresentation);

    void saveArrayValue(ArrayRestore valueRepresentation);

    void storeRepresentationForFreeVariable(Frame frame, int freeVariableIndex);

    FreeObjectref getAFreeObjectref(ClassFile classFile);

    ClassFile resolveClassAsClassFile(ClassFile fromClass, String type) throws VmRuntimeException, ExecutionException;
}
