package de.wwu.muli.searchtree;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muggl.vm.Frame;

import java.util.*;

public class Choice<A> extends ST<A> {
    private final Choice<A> parent;
    private final Stack<TrailElement> trail;
    private final Stack<TrailElement> inverseTrail;
    private final List<STProxy<A>> sts;
    private STProxy substitutedSTProxy;

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
            getSts().add(new STProxy<>(frame, pcIt.next(), constraintExpressionIt.next(), this));
        }

        this.trail = trailElements;
        this.inverseTrail = new Stack<>();
        this.parent = parent;
    }

    public Choice(Frame frame, int pcNext, int pcWithJump, ConstraintExpression constraintExpression, Stack<TrailElement> trailElements, Choice<A> parent) {
        this(frame, Arrays.asList(pcNext, pcWithJump), Arrays.asList(constraintExpression.negate(), constraintExpression), trailElements, parent);
    }

    public void setSubstitutedSTProxy(STProxy substitutedSTProxy) {
        this.substitutedSTProxy = substitutedSTProxy;
    }

    public STProxy getSubstitutedSTProxy() {
        return substitutedSTProxy;
    }

    public Choice<A> getParent() {
        return parent;
    }

    public List<STProxy<A>> getSts() {
        return sts;
    }

    public Stack<TrailElement> getTrail() {
        return trail;
    }

    public Stack<TrailElement> getInverseTrail() {
        return inverseTrail;
    }
}
