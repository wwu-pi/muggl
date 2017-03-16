package de.wwu.muggl.solvers.expressions.ref.meta;

import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Variable;

/**
 * A variable for object and array references.
 * 
 * @author Andreas Fuchs
 *
 */
public interface ReferenceVariable extends Variable {
	
	/**
	 * Get the boolean variable to check if this variable is null.
	 */
	public NumericVariable getIsNullVariable();
}