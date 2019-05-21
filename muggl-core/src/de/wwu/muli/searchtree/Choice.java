package de.wwu.muli.searchtree;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muggl.vm.Frame;

import java.util.Stack;

public class Choice<A> extends ST<A> {
    public final Choice<A> parent;
    private final Stack<TrailElement> trail;
    private final Stack<TrailElement> inverseTrail = new Stack<>();
    public STProxy<A> st1;
    public STProxy<A> st2;

    public Choice(Frame frame, int pcNext, int pcWithJump, ConstraintExpression constraintExpression, Choice<A> parent) {
        //LinkedList<TrailElement> state,
        this.st1 = new STProxy<A>(frame, pcNext, constraintExpression.negate(), this);
        this.st2 = new STProxy<A>(frame, pcWithJump, constraintExpression, this);
        this.trail = new Stack<>();//state;
        this.parent = parent;
    }


}
