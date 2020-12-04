package de.wwu.muggl.solvers.solver.constraints;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.Not;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.solvers.expressions.TypeCheckException;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;
import de.wwu.muggl.vm.initialization.IReferenceValue;

public class ArraySelect extends ArrayConstraint {
    private final String varName;
    private final Term lengthTerm; // TODO keep?

    private ArraySelect(IReferenceValue arrayref, String varName, Term index, Term lengthTerm, Term loadedValueTerm) {
        super(arrayref, index, loadedValueTerm);
        this.varName = varName;
        this.lengthTerm = lengthTerm;
    }

    public static ArraySelect newInstance(IReferenceValue arrayref, String varName, Term index, Term lengthTerm, Term loadedValue) {
        return new ArraySelect(arrayref, varName, index, lengthTerm, loadedValue);
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
        return "ArraySelect{arrayref=" + arrayref
                + ", varName=" + varName
                + ", index=" + index
                + ", loadedValueTerm=" + value + "}";
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
