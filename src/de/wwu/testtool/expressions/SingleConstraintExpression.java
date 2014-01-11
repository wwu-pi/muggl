package de.wwu.testtool.expressions;

import de.wwu.testtool.solver.constraints.Assignment;
import de.wwu.testtool.solver.constraints.SingleConstraint;

/**
 * A special {@link ConstraintExpression} representing a single constraint like {@link BooleanConstant} or {@link BooleanVariable}
 * @author Christoph Lembeck
 */
public abstract class SingleConstraintExpression extends ConstraintExpression implements SingleConstraint{

    @Override
    public abstract SingleConstraint insert(Assignment assignment);

    @Override
    public abstract ConstraintExpression insertAssignment(Assignment assignment);

}
