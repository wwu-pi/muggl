package de.wwu.muggl.solvers.solver.constraints;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.vm.initialization.IReferenceValue;

public abstract class ArrayConstraint extends ConstraintExpression {
    protected final IReferenceValue arrayref;
    protected final Term index;
    protected final Term value;

    public ArrayConstraint(IReferenceValue arrayref, Term index, Term value) {
        this.arrayref = arrayref;
        this.index = index;
        this.value = value;
    }

    public IReferenceValue getArrayref() {
        return arrayref;
    }

    public Term getIndexTerm() {
        return index;
    }

    public Term getValueTerm() {
        return value;
    }


}
