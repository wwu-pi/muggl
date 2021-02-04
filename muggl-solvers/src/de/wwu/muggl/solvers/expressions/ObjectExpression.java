package de.wwu.muggl.solvers.expressions;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.solver.constraints.Assignment;
import de.wwu.muggl.solvers.solver.constraints.Polynomial;
import de.wwu.muggl.solvers.solver.tools.Substitution;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;
import de.wwu.muggl.vm.initialization.IReferenceValue;

import java.util.Arrays;
import java.util.Set;

// For objects in FreeArrays. Not a general expression for FreeObjects.
public class ObjectExpression implements Expression {

    protected final String[] allFields;
    protected final Object[] allVals;
    protected final IReferenceValue expressedRef;

    public ObjectExpression(
            String[] allFields,
            Object[] allVals,
            IReferenceValue expressedRef) {
        this.allFields = allFields;
        this.allVals = allVals;
        this.expressedRef = expressedRef;
    }

    public static class UninitializedMarker {}

    public String[] getAllFields() {
        return allFields;
    }

    public Object[] getAllVals() {
        return allVals;
    }

    public IReferenceValue getExpressedRef() {
        return expressedRef;
    }

    // TODO Encode this to an array on the side of Z3
    // Field values are either other ObjectExpressions, or fields.

    @Override
    public void checkTypes() throws TypeCheckException {

    }

    @Override
    public Expression insert(Solution solution, boolean produceNumericSolution) {
        return null;
    }

    @Override
    public Term insertAssignment(Assignment assignment) {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toString(boolean useInternalVariableNames) {
        return "ObjectExpression{allFields=" + Arrays.toString(allFields)
                + ", allVals=" + Arrays.toString(allVals)
                + ", expressedRef=" + expressedRef + "}";
    }

    @Override
    public byte getType() {
        return 0;
    }

    @Override
    public String toTexString(boolean useInternalVariableNames) {
        return null;
    }

    @Override
    public String toHaskellString() {
        return null;
    }


}
