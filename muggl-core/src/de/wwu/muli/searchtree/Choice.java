package de.wwu.muli.searchtree;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muggl.vm.Frame;

import java.util.*;

public class Choice<A> extends ST<A> {
    public final Choice<A> parent;
    private final Stack<TrailElement> trail;
    private final Stack<TrailElement> inverseTrail;
    public List<STProxy<A>> sts;

    public Choice(Frame frame, List<Integer> pcs, List<ConstraintExpression> constraintExpressions, Stack<TrailElement> trailElements, Choice<A> parent) {
        Iterator<Integer> pcIt = pcs.iterator();
        Iterator<ConstraintExpression> constraintExpressionIt = constraintExpressions.iterator();

        if (pcs == null || constraintExpressions == null) {
            throw new IllegalArgumentException("No program counters or constraint expressions provided.");
        }

        if (pcs.size() != constraintExpressions.size()) {
            throw new IllegalArgumentException("Number of program counters and constraint expressions does not match.");
        }
        sts = new ArrayList<>(pcs.size());

        while (pcIt.hasNext() && constraintExpressionIt.hasNext()) {
            sts.add(new STProxy<>(frame, pcIt.next(), constraintExpressionIt.next(), this));
        }

        this.trail = trailElements;
        this.inverseTrail = new Stack<>();
        this.parent = parent;
    }

    public Choice(Frame frame, int pcNext, int pcWithJump, ConstraintExpression constraintExpression, Stack<TrailElement> trailElements, Choice<A> parent) {
        this(frame, Arrays.asList(pcNext, pcWithJump), Arrays.asList(constraintExpression, constraintExpression.negate()), trailElements, parent);
    }

}
