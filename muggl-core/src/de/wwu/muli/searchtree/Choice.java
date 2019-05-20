package de.wwu.muli.searchtree;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;

import java.util.LinkedList;

public class Choice<A> extends ST<A> {
    public final Choice<A> parent;
    public final LinkedList<TrailElement> trail;
    public STProxy<A> st1;
    public STProxy<A> st2;
    private final ConstraintExpression ce1;
    private final ConstraintExpression ce2;

    public Choice(int pcNext, int pcWithJump, ConstraintExpression constraintExpression, Choice<A> parent) {
        //LinkedList<TrailElement> state,
        this.st1 = new STProxy<A>(pcNext, this);
        this.st2 = new STProxy<A>(pcWithJump, this);
        this.ce1 = constraintExpression;
        this.ce2 = constraintExpression.negate();
        this.trail = new LinkedList<>();//state;
        this.parent = parent;
    }


}
