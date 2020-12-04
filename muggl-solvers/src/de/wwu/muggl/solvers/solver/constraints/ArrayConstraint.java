package de.wwu.muggl.solvers.solver.constraints;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.vm.initialization.IReferenceValue;

public abstract class ArrayConstraint extends ConstraintExpression {
    public abstract IReferenceValue getArrayref();
}
