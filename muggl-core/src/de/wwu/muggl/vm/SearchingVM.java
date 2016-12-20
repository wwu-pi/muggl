package de.wwu.muggl.vm;

import de.wwu.muggl.instructions.general.GeneralInstructionWithOtherBytes;
import de.wwu.muggl.search.SearchAlgorithm;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;

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

	void increaseTimeChoicePointGeneration(long increment);

	void increaseTimeSolvingForChoicePointGeneration(long increment);
}
