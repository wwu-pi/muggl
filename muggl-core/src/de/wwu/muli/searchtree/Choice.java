package de.wwu.muli.searchtree;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muggl.vm.Frame;

import java.util.*;

public class Choice<A> extends ST<A> {
    private final Choice<A> parent;
    private final Stack<TrailElement> trail;
    private final Stack<TrailElement> inverseTrail;
    private final List<UnevaluatedST<A>> sts;
    private UnevaluatedST substitutedUnevaluatedST;

    public Choice(Frame frame, List<Integer> pcs, List<ConstraintExpression> constraintExpressions, Stack<TrailElement> trailElements, Choice<A> parent) {
        if (pcs == null || constraintExpressions == null) {
            throw new IllegalArgumentException("No program counters or constraint expressions provided.");
        }

        if (pcs.size() != constraintExpressions.size()) {
            throw new IllegalArgumentException("Number of program counters and constraint expressions does not match.");
        }

        Iterator<Integer> pcIt = pcs.iterator();
        Iterator<ConstraintExpression> constraintExpressionIt = constraintExpressions.iterator();

        this.sts = new ArrayList<>(pcs.size());

        while (pcIt.hasNext() && constraintExpressionIt.hasNext()) {
            getSts().add(new UnevaluatedST<>(frame, pcIt.next(), constraintExpressionIt.next(), this));
        }

        this.trail = trailElements;
        this.inverseTrail = new Stack<>();
        this.parent = parent;
    }

    public Choice(Frame frame, int pcNext, int pcWithJump, ConstraintExpression constraintExpression, Stack<TrailElement> trailElements, Choice<A> parent) {
        this(frame, Arrays.asList(pcWithJump, pcNext), Arrays.asList(constraintExpression, constraintExpression.negate()), trailElements, parent);
    }

    public void setSubstitutedUnevaluatedST(UnevaluatedST substitutedUnevaluatedST) {
        this.substitutedUnevaluatedST = substitutedUnevaluatedST;
    }

    public UnevaluatedST<A> getSubstitutedUnevaluatedST() {
        return substitutedUnevaluatedST;
    }

    public Choice<A> getParent() {
        return parent;
    }

    public List<UnevaluatedST<A>> getSts() {
        return sts;
    }

    public Stack<TrailElement> getTrail() {
        return trail;
    }

    public Stack<TrailElement> getInverseTrail() {
        return inverseTrail;
    }
}
