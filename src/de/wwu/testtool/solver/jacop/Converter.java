/**
 * 
 */
package de.wwu.testtool.solver.jacop;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jacop.constraints.*;
import org.jacop.core.IntVar;
import org.jacop.core.Store;

import de.wwu.testtool.expressions.And;
import de.wwu.testtool.expressions.BooleanConstant;
import de.wwu.testtool.expressions.BooleanEqual;
import de.wwu.testtool.expressions.BooleanNotEqual;
import de.wwu.testtool.expressions.BooleanVariable;
import de.wwu.testtool.expressions.ConstraintExpression;
import de.wwu.testtool.expressions.GreaterOrEqual;
import de.wwu.testtool.expressions.GreaterThan;
import de.wwu.testtool.expressions.LessOrEqual;
import de.wwu.testtool.expressions.LessThan;
import de.wwu.testtool.expressions.Not;
import de.wwu.testtool.expressions.NumericEqual;
import de.wwu.testtool.expressions.NumericNotEqual;
import de.wwu.testtool.expressions.Or;
import de.wwu.testtool.expressions.SingleConstraintExpression;
import de.wwu.testtool.expressions.Term;
import de.wwu.testtool.expressions.Xor;
import de.wwu.testtool.solver.constraints.AndConstraint;
import de.wwu.testtool.solver.constraints.Assignment;
import de.wwu.testtool.solver.constraints.ComposedConstraint;
import de.wwu.testtool.solver.constraints.Equation;
import de.wwu.testtool.solver.constraints.NotConstraint;
import de.wwu.testtool.solver.constraints.NumericConstraint;
import de.wwu.testtool.solver.constraints.OrConstraint;
import de.wwu.testtool.solver.constraints.SingleConstraint;
import de.wwu.testtool.solver.constraints.StrictInequation;
import de.wwu.testtool.solver.constraints.WeakInequation;

/**
 * Converts Muggles constraints into JaCoP constraints
 * @author rafa
 * @version 1.0, 22 July 2014
 */
public class Converter {
	protected Logger logger;
	protected Store store;
	protected ArrayList<IntVar> vars;
	
	public Converter(Store store, ArrayList<IntVar> vars, Logger logger) {
		this.vars = vars;
		this.store = store;
		this.logger = logger;
	}


	/** 
	 * Converts a Muggle {@link ComposedConstraint} into JavaCoP format.
	 * It distinguishes cases according to Muggle {@link ComposedConstraint}type hierarchy.
	 * @param cc The ComposedConstraint in Muggle format
	 * @return A JavaCoP constraint representing the input.
	 * It is important to notice that it also can change the attribute {@link vars}
	 * as a side effect.
	 */
	public Constraint convert(ComposedConstraint cc) {
		Constraint r = null;
		if (cc instanceof AndConstraint) 
			r  = andConstraint((AndConstraint) cc);
		else if (cc instanceof OrConstraint)
			r = orConstraint((OrConstraint) cc);
		else if (cc instanceof SingleConstraint) 
			r = singleConstraint((SingleConstraint) cc);
		else {
			if (logger.isEnabledFor(Level.FATAL))
				logger.fatal("Unexpected instanceoftype: "+cc.getClass().toString());		
			}
		return r;
	}

	
	/** 
	 * Converts a Muggle {@link AndConstraint}  into JavaCoP format.
	 * @param cc The  AndConstraint in Muggle format
	 * @return A JavaCoP constraint representing cc.
	 * It is important to notice that it also can change the attribute {@link vars}
	 * as a side effect.
	 */
	private Constraint andConstraint(AndConstraint cc) {
		Constraint r=null;
		ComposedConstraint cc1 = cc.getLeft();
		ComposedConstraint cc2 = cc.getRight();
		PrimitiveConstraint c1 = (PrimitiveConstraint)  convert(cc1);
		PrimitiveConstraint c2 = (PrimitiveConstraint)  convert(cc2);
		r = new org.jacop.constraints.And(c1,c2);
		return r;
	}
	/** 
	 * Converts a Muggle {@link OrConstraint}  into JavaCoP format.
	 * @param cc The  OrConstraint in Muggle format
	 * @return A JavaCoP constraint representing cc.
	 * It is important to notice that it also can change the attribute {@link vars}
	 * as a side effect.
	 */
	private Constraint orConstraint(OrConstraint cc) {
		Constraint r=null;

		ComposedConstraint cc1 = cc.getLeft();
		ComposedConstraint cc2 = cc.getRight();
		PrimitiveConstraint c1 = (PrimitiveConstraint)  convert(cc1);
		PrimitiveConstraint c2 = (PrimitiveConstraint)  convert(cc2);
		r = new org.jacop.constraints.Or(c1,c2);

        return r; 		
	}
	
	/** 
	 * Converts a Muggle {@link SingleConstraint}  into JavaCoP format.
	 * @param cc The  SingleConstraint in Muggle format
	 * @return A JavaCoP constraint representing cc.
	 * It is important to notice that it also can change the attribute {@link vars}
	 * as a side effect.
	 */
	private Constraint singleConstraint(SingleConstraint cc) {
		Constraint r = null;
		if (cc instanceof Assignment) {} 
		else if (cc instanceof BooleanConstant) {}
		else if (cc instanceof BooleanVariable) {} 
		else if (cc instanceof NotConstraint) {} 
		else if (cc instanceof SingleConstraintExpression) 
			r = singleConstraintExpression((SingleConstraintExpression)cc);
		else if (cc instanceof NumericConstraint) 
			r = numericConstraint((NumericConstraint)cc);
		else {
			if (logger.isEnabledFor(Level.FATAL))
				logger.fatal("Unexpected instanceoftype in singleConstraint: "+cc.getClass().toString());		
			}
		return r;
	}

	/** 
	 * Converts a Muggle {@link numericConstraint}  into JavaCoP format.
	 * @param cc The  numericConstraint in Muggle format
	 * @return A JavaCoP constraint representing cc.
	 * It is important to notice that it also can change the attribute {@link vars}
	 * as a side effect.
	 */
	private Constraint  numericConstraint( NumericConstraint cc) {
		Constraint r = null;
		if (cc instanceof Equation) {}
		else if (cc instanceof StrictInequation) {}
		else if (cc instanceof WeakInequation) {}
		else {
			if (logger.isEnabledFor(Level.FATAL))
				logger.fatal("Unexpected instanceoftype in numericConstraint: "+cc.getClass().toString());		
			}
		return r;
	}
	/** 
	 * A new constraint is incorporated to the store.
	 * It distinguishes cases according to Muggle expressions
	 * @param ce The new constraint in Muggle format
	 */
	private void convert(ConstraintExpression ce) {
											
		if (ce instanceof And) {}
		else if (ce instanceof BooleanEqual) {}
		else if (ce instanceof BooleanNotEqual) {}
		else if (ce instanceof GreaterOrEqual) {}
		else if (ce instanceof GreaterThan) {}
		else if (ce instanceof LessOrEqual) {}
		else if (ce instanceof LessThan) {}
		else if (ce instanceof Not) {}
		else if (ce instanceof NumericEqual) 
			numericEqual((NumericEqual)ce);
		else if (ce instanceof NumericNotEqual) 
				numericNotEqual((NumericNotEqual)ce);
		else if (ce instanceof Or) {}
		else if (ce instanceof SingleConstraintExpression) 
				singleConstraintExpression((SingleConstraintExpression)ce);
		else if (ce instanceof Xor) {}
		else {
			if (logger.isEnabledFor(Level.FATAL))
				logger.fatal("Unexpected instanceoftype: "+ce.getClass().toString());		
			}
		

	}
	/**
	 * a != b
	 * @param c input constraint
	 */
	private void numericNotEqual(NumericNotEqual c) {
		Term t1 = c.getLeft();
		Term t2 = c.getRight();
	}
	/**
	 * a != b
	 * @param c input constraint
	 */
	private void numericEqual(NumericEqual c) {
		
	}
	/** 
	 * Converts a Muggle {@link SingleConstraintExpression}  into JavaCoP format.
	 * @param cc The  SingleConstraintExpression in Muggle format
	 * @return A JavaCoP constraint representing cc.
	 * It is important to notice that it also can change the attribute {@link vars}
	 * as a side effect.
	 */
	private Constraint singleConstraintExpression(SingleConstraintExpression c) {
		Constraint r = null;
		if (c instanceof BooleanConstant) {}
		if (c instanceof BooleanVariable) {}	
		return r;
	}


}
