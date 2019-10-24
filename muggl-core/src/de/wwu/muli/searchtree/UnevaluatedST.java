package de.wwu.muli.searchtree;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.vm.Frame;

import java.util.Optional;

public class UnevaluatedST<A> extends ST<A> {
    /**
     * Frame at which execution has to continue for evaluation.
     */
    private final Frame frame;
    /**
     * PC at which execution has to continue for evaluation.
     */
    private final int pc;
    /**
     * ConstraintExpression that corresponds to entering this subtree. If null, no constraint is required for entering this subtree.
     */
    private final ConstraintExpression constraintExpression;
    /**
     * Records the direct parent in order to be able to obtain its trail if needed. If null, this is the full tree (not a subtree).
     */
    private final Choice<A> childOf;

    private ST<A> evaluationResult = null;

    public boolean isEvaluated() {
        return this.evaluationResult != null;
    }

    public void setEvaluationResult(ST<A> result) {
        evaluationResult = result;
    }

    public ST<A> getEvaluationResult() {
        return evaluationResult;
    }

    public UnevaluatedST(Frame frame, int pc, ConstraintExpression constraintExpression, Choice<A> childOf) {
        this.frame = frame;
        this.pc = pc;
        // If constraintExpression is null, no constraint is required for entering this subtree.
        this.constraintExpression = constraintExpression;
        // If childOf is null, this UnevaluatedST represents a full tree instead of a subtree.
        this.childOf = childOf;

    }

    /**
     * For a (proxy) node n with parents p_1, p_2, ..., p_i, r where r is root,
     * the trail of n is trail(r) ++ trail(p_i) ++ .. ++ trail(p_2) ++ trail(p1).
     * n does not have a local trail.
     * @return
     */
    /*public LinkedList<TrailElement> getTrail() {
        Choice<A> before = this.childOf;
        LinkedList<TrailElement> trail = new LinkedList<>();
        while (before != null) {
            trail.addAll(0, before.trail);
            before = before.parent;
        }
        return trail;
    }*/

    public Choice getParent() {
        return childOf;
    }

    public Frame getFrame() {
        return frame;
    }

    public int getPc() {
        return pc;
    }

    public Optional<ConstraintExpression> getConstraintExpression() {
        return Optional.ofNullable(constraintExpression);
    }
}
