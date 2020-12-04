package de.wwu.muggl.solvers;

import de.wwu.muggl.solvers.solver.constraints.ArrayConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ArrayConstraintAccumulator {
    protected final SolverManager delegateTo;
    protected final Function<List<ArrayConstraint>, Boolean> flushCondition;
    protected final List<ArrayConstraint> accumulatedExpressions = new ArrayList<>();

    public ArrayConstraintAccumulator(SolverManager delegateTo) {
        this((list) -> {return true;}, delegateTo); // Always flush immediately
    }

    public ArrayConstraintAccumulator(
            Function<List<ArrayConstraint>, Boolean> flushCondition,
            SolverManager delegateTo) {
        this.flushCondition = flushCondition;
        this.delegateTo = delegateTo;
    }

    public void accumulate(ArrayConstraint arrayConstraint) {
        accumulatedExpressions.add(arrayConstraint);
        if (flushCondition.apply(accumulatedExpressions)) {
            flush();
        }
    }

    public void flush() {
        for (ArrayConstraint ae : accumulatedExpressions) {
            delegateTo.addConstraintPastChecks(ae);
        }
        accumulatedExpressions.clear();
    }

}
