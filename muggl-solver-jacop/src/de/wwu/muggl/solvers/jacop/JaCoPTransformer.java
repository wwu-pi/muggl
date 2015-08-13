package de.wwu.muggl.solvers.jacop;

import org.jacop.constraints.Constraint;
import org.jacop.constraints.XgteqC;
import org.jacop.constraints.XlteqC;
import org.jacop.core.IntVar;
import org.jacop.core.IntervalDomain;

import de.wwu.testtool.expressions.ConstraintExpression;
import de.wwu.testtool.expressions.GreaterOrEqual;
import de.wwu.testtool.expressions.GreaterThan;
import de.wwu.testtool.expressions.HasLeftAndRightTerms;
import de.wwu.testtool.expressions.IntConstant;
import de.wwu.testtool.expressions.LessOrEqual;
import de.wwu.testtool.expressions.NumericVariable;
import de.wwu.testtool.expressions.Term;
import de.wwu.testtool.expressions.Variable;

public class JaCoPTransformer {
	public static void transformAndImpose(ConstraintExpression ce, JacopMugglStore store) {
		
		if (ce instanceof HasLeftAndRightTerms) {
			generateEquation((HasLeftAndRightTerms) ce, store);
		} else {
			throw new IllegalArgumentException("Unknown constraint type " + ce.getClass().getName());
		}
	}

	private static void generateEquation(HasLeftAndRightTerms ce, JacopMugglStore store) {
		Term left = ce.getLeft();
		Term right = ce.getRight();
		
		if (left.isConstant() && right.isConstant()) { // sanity check (AND no JaCoP constraint would exist for this)
			throw new IllegalStateException("Both terms of the equation " + ce + " are constant. Why was this constraint added in the first place?");
		}
		
		// Store whether sides will need to be switched, as constants may only appear as the right term in JaCoP.
		// This implies mirroring the comparison for lt(e)/gt(e)
		// Furthermore, non-constant constraints need to be normalised to lt(e) constraints.
		boolean switchSides = left.isConstant() || ce instanceof GreaterOrEqual || ce instanceof GreaterThan;
		boolean hasConstant = left.isConstant() || right.isConstant();

		
		boolean isInteger = isInteger(left) && isInteger(right);
		
		Constraint resultingConstraint;
		
		if (isInteger) {
			if (hasConstant) {
				int constantValue = left.isConstant() ? ((IntConstant)left).getValue() : ((IntConstant)right).getValue();
				Term variableTerm = left.isConstant() ? right : left;

				if (ce instanceof LessOrEqual) {
					IntVar termVar = normaliseIntegerTerm(variableTerm, store);
					// assert: termVar == variableTerm
					
					if (switchSides) { 
						resultingConstraint = new XgteqC(termVar, constantValue);
					} else {
						resultingConstraint = new XlteqC(termVar, constantValue);
						//resultingConstraint = new XgteqC(termVar, constantValue);//TODO remove this; was just for testing
					}
				} else {
					// other type of equation with constant
					throw new IllegalArgumentException("Unknown (with-constant, all-int) constraint type " + ce.getClass().getName());
				}
			} else {
				// no constant, all int
				throw new IllegalArgumentException("Unknown (non-constant, all-int) constraint type " + ce.getClass().getName());
			}
			
		} else {
			// At least one variable is a float variable.
			//TODO
			throw new IllegalArgumentException("Unknown (non-int) constraint type " + ce.getClass().getName());
			//TODO Special case: One is integer
		}
		
		store.impose(resultingConstraint);
		
	}

	private static IntVar normaliseIntegerTerm(Term integerTerm, JacopMugglStore store) {
		if (integerTerm instanceof NumericVariable) {
			if ( !((NumericVariable)integerTerm).isInteger() ) {
				throw new IllegalStateException("Trying to normalise an integer term that contains a non-integer Variable"); 
			}
			
			IntVar intVar = (IntVar) store.getVariable((Variable)integerTerm);
			
			if (intVar == null) {
				intVar = new IntVar(store, 
					((NumericVariable)integerTerm).getInternalName(), 
					new IntervalDomain(Integer.MIN_VALUE, Integer.MAX_VALUE)); //  + 1 MIN_VALUE + 1 because otherwise Store will integer overflow while checking boundaries
				// add to cache
				store.addVariable((Variable)integerTerm, intVar);
			}
			return intVar;
		} else {
			throw new IllegalArgumentException("Unknown integer term type " + integerTerm.getClass().getName());
		}
		
	}

	private static boolean isInteger(Term term) {
		if (term instanceof IntConstant) {
			return true;
		} else if (term instanceof NumericVariable) {
			return ((NumericVariable)term).isInteger();
		}
		throw new IllegalArgumentException("Unknown term type " + term.getClass().getName());
	}
	
}
