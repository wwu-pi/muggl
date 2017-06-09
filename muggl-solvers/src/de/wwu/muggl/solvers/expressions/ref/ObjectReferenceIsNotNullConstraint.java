package de.wwu.muggl.solvers.expressions.ref;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.ref.meta.ReferenceVariable;

public class ObjectReferenceIsNotNullConstraint extends ObjectReferenceConstraint {

	public ObjectReferenceIsNotNullConstraint(ReferenceVariable objRef) {
		super(objRef);
	}

	@Override
	public ConstraintExpression negate() {
		return new ObjectReferenceIsNullConstraint(this.objRef);
	}
	
	@Override
	public String toString(boolean useInternalVariableNames) {	
		return "Constraint to be a NOT null-reference on: ["+this.objRef+"]";
	}
	
}
