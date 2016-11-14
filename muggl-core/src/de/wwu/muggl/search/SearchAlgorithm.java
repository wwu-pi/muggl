package de.wwu.muggl.search;

import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.Restore;

/**
 * This interface is to be extended by other interfaces, specifying methods to be
 * used by different kinds of virtual machines. 
 * At the time of conception, two kinds are considered:
 * * Symbolic JVMs
 * * Logic JVMs
 * @author Jan Dageförde
 * @version 1.0.0
 */
public interface SearchAlgorithm {

	/**
	 * Return a String representation of this search algorithms name.
	 *
	 * @return A String representation of this search algorithms name.
	 */
	String getName();

	/**
	 * Get the information whether this search algorithm requires a local
	 * variable value to be stored (at this exakt moment of execution).
	 * @return true, if the local variable value should be stored, false otherwise.
	 */
	 boolean savingLocalVariableValues();

	/**
	 * Store a local variable value for use by the search algorithm's tracking back
	 * functionality.
	 * @param valueRepresentation A Restore object.
	 */
	void saveLocalVariableValue(Restore valueRepresentation);
	
}