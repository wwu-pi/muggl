package de.wwu.muggl.solvers.expressions;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.solver.constraints.Assignment;
import de.wwu.muggl.solvers.solver.constraints.ComposedConstraint;
import de.wwu.muggl.solvers.solver.tools.SubstitutionTable;
import de.wwu.muggl.vm.initialization.IReferenceValue;

import java.util.HashSet;
import java.util.Set;

public class ClassConstraintExpression extends ConstraintExpression {

    private final IReferenceValue target;
    private final Set<String> types;


    /**
     * Creates a constraint expression restricting the allowed types of an Objectref.
     * @param target the to-be-constrained Objectref
     * @param types the set of allowed types
     * @return the new ClassConstraintExpression expression.
     */
    public static ConstraintExpression newInstance(IReferenceValue target, String type){
        Set<String> types = new HashSet<>();
        types.add(type);
        return newInstance(target, types);
    }

    /**
     * Creates a constraint expression restricting the allowed types of an Objectref.
     * @param target the to-be-constrained Objectref
     * @param types the set of allowed types
     * @return the new ClassConstraintExpression expression.
     */
    public static ConstraintExpression newInstance(IReferenceValue target, Set<String> types){
        return new ClassConstraintExpression(target, types);
    }
    /**
     * Creates a constraint expression restricting the allowed types of an Objectref.
     * @param target the to-be-constrained Objectref
     * @param types the set of allowed types
     * @see #newInstance(IReferenceValue, Set)
     */
    private ClassConstraintExpression(IReferenceValue target, Set<String> types) {
        this.target = target;
        this.types = types;
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
        return "typeof " + this.target.toString() + " in {" + types + "}";
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
        return null;
    }
}
