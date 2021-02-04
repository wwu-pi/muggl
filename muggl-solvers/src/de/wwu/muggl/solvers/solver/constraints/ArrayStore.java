package de.wwu.muggl.solvers.solver.constraints;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;
import de.wwu.muggl.vm.initialization.IReferenceValue;

public class ArrayStore extends ArrayConstraint {
    private final String varName;
    private final Term lengthTerm; // TODO Keep?

    public ArrayStore(IReferenceValue arrayref, String varName, Term index, Term lengthTerm, Expression storedValue) {
        super(arrayref, index, storedValue);
        this.varName = varName;
        this.lengthTerm = lengthTerm;
    }

    public static ArrayStore newInstance(IReferenceValue arrayref, String varName, Term index, Term lengthTerm, Expression storedValue) {
        return new ArrayStore(arrayref, varName, index, lengthTerm, storedValue);
    }

    public String getName() {
        return varName;
    }

    public Term getLengthTerm() {
        return lengthTerm;
    }

    @Override
    public ComposedConstraint convertToComposedConstraint(SubstitutionTable subTable) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ConstraintExpression insertAssignment(Assignment assignment) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean isConstant() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String toString(boolean useInternalVariableNames) {
        return "ArrayStore{arrayref=" + arrayref
                + ", varName=" + varName
                + ", index=" + index
                + ", storedValueTerm=" + value + "}";
    }

    @Override
    public byte getType() {
        return Type.ARRAY.toByte();
    }

    @Override
    public String toTexString(boolean useInternalVariableNames) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String toHaskellString() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void checkTypes() throws TypeCheckException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ConstraintExpression insert(Solution solution, boolean produceNumericSolution) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ConstraintExpression negate() {
        return Not.newInstance(this);
    }

}
