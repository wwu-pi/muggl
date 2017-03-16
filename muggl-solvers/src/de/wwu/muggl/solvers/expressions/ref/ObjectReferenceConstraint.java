package de.wwu.muggl.solvers.expressions.ref;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.TypeCheckException;
import de.wwu.muggl.solvers.expressions.ref.meta.ReferenceVariable;
import de.wwu.muggl.solvers.solver.constraints.Assignment;
import de.wwu.muggl.solvers.solver.constraints.ComposedConstraint;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;

public abstract class ObjectReferenceConstraint extends ConstraintExpression {

	protected ReferenceVariable objRef;
	
	public ObjectReferenceConstraint(ReferenceVariable objRef) {
		this.objRef = objRef;
	}
	
	public ReferenceVariable getObjectRef() {
		return this.objRef;
	}
	
	
	
	
	@Override
	public void checkTypes() throws TypeCheckException {}

	@Override
	public boolean isConstant() {return false;}

	@Override
	public byte getType() {	return -1;}

	@Override
	public String toTexString(boolean useInternalVariableNames) {	return null;}

	@Override
	public String toHaskellString() {return null;}

	@Override
	public ComposedConstraint convertToComposedConstraint(SubstitutionTable subTable) {	return null;}

	@Override
	public ConstraintExpression insertAssignment(Assignment assignment) {return null;}

	@Override
	public ConstraintExpression insert(Solution solution, boolean produceNumericSolution) {	return null;}
}
