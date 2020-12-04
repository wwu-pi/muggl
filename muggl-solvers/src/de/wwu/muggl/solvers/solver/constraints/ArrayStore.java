package de.wwu.muggl.solvers.solver.constraints;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.Not;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.solvers.expressions.TypeCheckException;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;
import de.wwu.muggl.vm.initialization.IReferenceValue;

public class ArrayStore extends ArrayConstraint {
    private final IReferenceValue arrayref;
    private final String varName;
    private final Term index;
    private final Term lengthTerm;
    private final Term storedValueTerm;

    public ArrayStore(IReferenceValue arrayref, String varName, Term index, Term lengthTerm, Term storedValueTerm) {
        this.arrayref = arrayref;
        this.varName = varName;
        this.index = index;
        this.lengthTerm = lengthTerm;
        this.storedValueTerm = storedValueTerm;
    }

    public static ArrayStore newInstance(IReferenceValue arrayref, String varName, Term index, Term lengthTerm, Term storedValueTerm) {
        return new ArrayStore(arrayref, varName, index, lengthTerm, storedValueTerm);
    }

    public IReferenceValue getArrayref() {
        return arrayref;
    }

    public Term getIndexTerm() {
        return index;
    }

    public Term getStoredValueTerm() {
        return storedValueTerm;
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
                + ", storedValueTerm=" + storedValueTerm + "}";
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
