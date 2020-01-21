package de.wwu.muggl.solvers.expressions;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.solver.constraints.Assignment;
import de.wwu.muggl.solvers.solver.constraints.ComposedConstraint;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;
import de.wwu.muggl.vm.initialization.IReferenceValue;

import java.util.HashSet;
import java.util.Set;

public class ClassConstraintExpression extends ConstraintExpression implements TypeConstraint {

    private final IReferenceValue target;
    private final Set<String> types;
    private final Set<String> notTypes;


    /**
     * Creates a constraint expression restricting the allowed types of an Objectref.
     * @param target the to-be-constrained Objectref
     * @param type a single allowed type
     * @return the new ClassConstraintExpression expression.
     */
    public static ConstraintExpression newInstance(IReferenceValue target, String type){
        Set<String> emptySet = new HashSet<>();
        return newInstance(target, type, emptySet);
    }


    /**
     * Creates a constraint expression restricting the allowed types of an Objectref.
     * @param target the to-be-constrained Objectref
     * @param type a single allowed type
     * @param notTypes types that are excluded even though they may be subtypes of `type'.
     * @return the new ClassConstraintExpression expression.
     */
    public static ConstraintExpression newInstance(IReferenceValue target, String type, Set<String> notTypes) {
        Set<String> types = new HashSet<>();
        types.add(type);
        return newInstance(target, types, notTypes);
    }
    /**
     * Creates a constraint expression restricting the allowed types of an Objectref.
     * @param target the to-be-constrained Objectref
     * @param types the set of allowed types
     * @param notTypes types that are excluded even though they may be subtypes of a type in `types'.
     * @return the new ClassConstraintExpression expression.
     */
    public static ConstraintExpression newInstance(IReferenceValue target, Set<String> types, Set<String> notTypes){
        return new ClassConstraintExpression(target, types, notTypes);
    }
    /**
     * Creates a constraint expression restricting the allowed types of an Objectref.
     * @param target the to-be-constrained Objectref
     * @param types the set of allowed types
     * @param notTypes types that are excluded even though they may be subtypes of a type in `types'.
     * @see #newInstance(IReferenceValue, Set, Set)
     */
    private ClassConstraintExpression(IReferenceValue target, Set<String> types, Set<String> notTypes) {
        this.target = target;
        this.types = types;
        this.notTypes = notTypes;
    }
    @Override
    public ComposedConstraint convertToComposedConstraint(SubstitutionTable subTable) {
        return null;
    }

    @Override
    public ConstraintExpression insertAssignment(Assignment assignment) {
        return null;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toString(boolean useInternalVariableNames) {
        StringBuilder sb = new StringBuilder();
        for(String t : this.types) {
            sb.append(t);
            sb.append(",");
        }
        String types = sb.length() > 0 ? sb.substring(0, sb.length()-1) : "";
        StringBuilder sb2 = new StringBuilder();
        for(String t : this.notTypes) {
            sb2.append(t);
            sb2.append(",");
        }
        String notTypes = sb2.length() > 0 ? sb2.substring(0, sb2.length()-1) : "";
        return "typeof " + this.target.toString() + " in {" + types + "} \\ {" + notTypes + "}";
    }

    @Override
    public byte getType() {
        return Expression.Type.CLASSCONSTRAINT.toByte();
    }

    @Override
    public String toTexString(boolean useInternalVariableNames) {
        return null;
    }

    @Override
    public String toHaskellString() {
        return null;
    }

    @Override
    public void checkTypes() throws TypeCheckException {
        // Composition is unexpected anyway, so nested expressions will not result in type problems.
        return;
    }

    @Override
    public ConstraintExpression insert(Solution solution, boolean produceNumericSolution) {
        return null;
    }

    @Override
    public ConstraintExpression negate() {
        // TODO incomplete: In addition to notTypes, need to add the universe of all types (so that only this.types is excluded).
        return new ClassConstraintExpression(this.target, this.notTypes, this.types);
    }

    public IReferenceValue getTarget() {
        return this.target;
    }

    public Set<String> getTypes() {
        return this.types;
    }

    public Set<String> getNotTypes() {
        return this.notTypes;
    }
}
