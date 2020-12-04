package de.wwu.muggl.solvers.expressions;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.solver.constraints.Assignment;
import de.wwu.muggl.solvers.solver.constraints.Polynomial;
import de.wwu.muggl.solvers.solver.tools.Substitution;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;
import de.wwu.muggl.vm.initialization.IReferenceValue;

import java.util.Arrays;
import java.util.Set;

public class ObjectExpression extends Term {

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
    public Term clearMultiFractions(Set<Term> denominators) {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    public Term insertAssignment(Assignment assignment) {
        throw new IllegalStateException("Case not yet implemented.");
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
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    public String toTexString(boolean useInternalVariableNames) {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    public String toHaskellString() {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    public void checkTypes() throws TypeCheckException {

    }

    @Override
    public Term insert(Solution solution, boolean produceNumericSolution) {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    public Term substitute(Term a, Term b) {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    public Polynomial toPolynomial() {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected boolean containsAsDenominator(Term t) {
        return false;
    }

    @Override
    protected Substitution findSubstitution(SubstitutionTable subTable) {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected Set<Term> getDenominators() {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected Modulo getFirstModulo() {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected Quotient getFirstNonintegerQuotient() {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected Quotient getFirstQuotient() {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected TypeCast getFirstTypeCast(boolean onlyNarrowing) {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected Modulo getInmostModulo() {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected Quotient getInmostQuotient() {
        throw new IllegalStateException("Case not yet implemented.");
    }

    @Override
    protected Term multiply(Term factor) {
        throw new IllegalStateException("Case not yet implemented.");
    }
}
