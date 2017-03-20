package de.wwu.muggl.solvers.expressions;

import java.util.Set;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.solver.constraints.Assignment;
import de.wwu.muggl.solvers.solver.constraints.ComposedConstraint;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;

/**
 * Constraint to set a variable to be the maximum of a given set of variables.
 * 
 * @author Andreas Fuchs
 *
 */
public class Max extends ConstraintExpression {

	protected Set<NumericVariable> variableSet;
	protected NumericVariable maxVar;
	
	public Max(Set<NumericVariable> variableSet, NumericVariable maxVar) {
		this.variableSet = variableSet;
		this.maxVar = maxVar;
	}

	public Set<NumericVariable> getVariableSet() {
		return variableSet;
	}

	public NumericVariable getMaxVar() {
		return maxVar;
	}


	public static ConstraintExpression newInstance(Set<NumericVariable> variableSet, NumericVariable maxVar) {
		return new Max(variableSet, maxVar);
	}

	@Override
	public void checkTypes() throws TypeCheckException {	
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public String toString(boolean useInternalVariableNames) {
		return "MaxConstraint: set variable ["+this.maxVar+"] to be maxium of: " + this.variableSet;
	}

	@Override
	public byte getType() {
		return Expression.INT;
	}

	@Override
	public String toTexString(boolean useInternalVariableNames) {
		return null;
	}

	@Override
	public String toHaskellString() {
		return null;
	}

	@Override
	public ComposedConstraint convertToComposedConstraint(SubstitutionTable subTable) {
		return null;
	}

	@Override
	public ConstraintExpression insertAssignment(Assignment assignment) {
		return null;
	}

	@Override
	public ConstraintExpression insert(Solution solution, boolean produceNumericSolution) {
		return null;
	}

	@Override
	public ConstraintExpression negate() {
		throw new RuntimeException("Not possible");
	}
	
}
