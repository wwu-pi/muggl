package de.wwu.muggl.vm;

import de.wwu.muggl.search.SearchAlgorithm;
import de.wwu.muggl.solvers.SolverManager;

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
}
