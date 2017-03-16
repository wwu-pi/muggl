package de.wwu.muggl.solvers.expressions.ref;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.ref.meta.ReferenceVariable;

public class ObjectReferenceIsNullConstraint extends ObjectReferenceConstraint {

	public ObjectReferenceIsNullConstraint(ReferenceVariable objRef) {
		super(objRef);
	}

	@Override
	public ConstraintExpression negate() {
		return new ObjectReferenceIsNotNullConstraint(this.objRef);
	}
	
	@Override
	public String toString(boolean useInternalVariableNames) {	
		return "Constraint to be a null-reference on: ["+this.objRef+"]";
	}
}
