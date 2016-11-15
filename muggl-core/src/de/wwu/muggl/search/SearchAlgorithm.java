package de.wwu.muggl.search;

import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.ArrayRestore;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.FieldPut;
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

	/**
	 * Get the information whether this search algorithm requires a field
	 * value to be stored (at this exakt moment of execution).
	 * @return true, if the field value should be stored, false otherwise.
	 */
	boolean savingFieldValues();

	/**
	 * Store a field value for use by the search algorithm's tracking back
	 * functionality.
	 * @param valueRepresentation Either a InstanceFieldPut or a StaticfieldPut object.
	 */
	void saveFieldValue(FieldPut valueRepresentation);

	/**
	 * Get the information whether this search algorithm requires an array
	 * value to be stored (at this exakt moment of execution).
	 * @return true, if the array value should be stored, false otherwise.
	 */
	boolean savingArrayValues();

	/**
	 * Store a array value for use by the search algorithm's tracking back
	 * functionality.
	 * @param valueRepresentation An ArrayRestore object.
	 */
	void saveArrayValue(ArrayRestore valueRepresentation);
	
}