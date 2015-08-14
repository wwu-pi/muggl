package de.wwu.muggl.solvers.jacop;

import org.jacop.constraints.Constraint;
import org.jacop.constraints.XeqY;
import org.jacop.constraints.XgtC;
import org.jacop.constraints.XgteqC;
import org.jacop.constraints.XltC;
import org.jacop.constraints.XlteqC;
import org.jacop.constraints.XmulYeqZ;
import org.jacop.constraints.XneqY;
import org.jacop.constraints.XplusYeqZ;
import org.jacop.core.IntVar;
import org.jacop.core.IntervalDomain;

import de.wwu.testtool.expressions.ConstraintExpression;
import de.wwu.testtool.expressions.GreaterOrEqual;
import de.wwu.testtool.expressions.GreaterThan;
import de.wwu.testtool.expressions.HasLeftAndRightTerms;
import de.wwu.testtool.expressions.IntConstant;
import de.wwu.testtool.expressions.LessOrEqual;
import de.wwu.testtool.expressions.LessThan;
import de.wwu.testtool.expressions.NumericEqual;
import de.wwu.testtool.expressions.NumericNotEqual;
import de.wwu.testtool.expressions.NumericVariable;
import de.wwu.testtool.expressions.Product;
import de.wwu.testtool.expressions.Sum;
import de.wwu.testtool.expressions.Term;
import de.wwu.testtool.expressions.Variable;

public class JaCoPTransformer {
	private static final IntervalDomain DOMAIN_INTEGER = new IntervalDomain(-10, 20);

	public static void transformAndImpose(ConstraintExpression ce, JacopMugglStore store) {
		
		if (ce instanceof HasLeftAndRightTerms) {
			generateEquation((HasLeftAndRightTerms) ce, store);
		} else {
			throw new IllegalArgumentException("Unknown constraint type " + ce.getClass().getName());
		}
	}

	private static void generateEquation(HasLeftAndRightTerms ce, JacopMugglStore store) {
		System.out.println("Transforming equation " + ce);
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
					}
				} else if (ce instanceof LessThan) {
					IntVar termVar = normaliseIntegerTerm(variableTerm, store);
					// assert: termVar == variableTerm
					
					if (switchSides) { 
						resultingConstraint = new XgtC(termVar, constantValue);
					} else {
						resultingConstraint = new XltC(termVar, constantValue);
					}
				} else {
					// other type of equation with constant
					throw new IllegalArgumentException("Unknown (with-constant, all-int) constraint type " + ce.getClass().getName());
				}
			} else {
				// no constant, all int
				if (ce instanceof NumericEqual) {
					IntVar leftTermVar = normaliseIntegerTerm(ce.getLeft(), store);
					IntVar rightTermVar = normaliseIntegerTerm(ce.getRight(), store);
					
					resultingConstraint = new XeqY(leftTermVar, rightTermVar);
				} else if (ce instanceof NumericNotEqual) {
					IntVar leftTermVar = normaliseIntegerTerm(ce.getLeft(), store);
					IntVar rightTermVar = normaliseIntegerTerm(ce.getRight(), store);
					
					resultingConstraint = new XneqY(leftTermVar, rightTermVar);
				} else {
					throw new IllegalArgumentException("Unknown (non-constant, all-int) constraint type " + ce.getClass().getName());
				}
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
		if (integerTerm instanceof IntConstant) {
			// for a constant, just add a variable with a very restricted domain. Should save one constraint.
			int constantValue = ((IntConstant)integerTerm).getIntValue();
			return new IntVar(store, constantValue, constantValue);
		} else if (integerTerm instanceof NumericVariable) {
			if ( !((NumericVariable)integerTerm).isInteger() ) {
				throw new IllegalStateException("Trying to normalise an integer term that contains a non-integer Variable"); 
			}
			
			IntVar intVar = (IntVar) store.getVariable((Variable)integerTerm);
			
			if (intVar == null) {
				intVar = new IntVar(store, 
					((NumericVariable)integerTerm).getInternalName(), 
					DOMAIN_INTEGER);
				// add to cache
				store.addVariable((Variable)integerTerm, intVar);
			}
			return intVar;
		} else if (integerTerm instanceof Sum) {
			// Returns an intermediate variable, and impose a constraint that this intermediate
			// variable be the sum of its two terms. Note that a term may be anything (e.g. a Sum),
			// so that this function may work recursively until reaching a constant or variable 
			IntVar intermediateVariable = new IntVar(store, DOMAIN_INTEGER);
			// TODO optimization: sequences of additions -> Sum
			XplusYeqZ sumConstraint = new XplusYeqZ(
					normaliseIntegerTerm(((Sum)integerTerm).getLeft(), store), 
					normaliseIntegerTerm(((Sum)integerTerm).getRight(), store), 
					intermediateVariable);
			store.impose(sumConstraint);
			return intermediateVariable;
		} else if (integerTerm instanceof Product) {
			// Returns an intermediate variable, and impose a constraint that this intermediate
			// variable be the sum of its two terms. Note that a term may be anything (e.g. a Sum),
			// so that this function may work recursively until reaching a constant or variable 
			IntVar intermediateVariable = new IntVar(store, DOMAIN_INTEGER);
			XmulYeqZ productConstraint = new XmulYeqZ(
					normaliseIntegerTerm(((Product)integerTerm).getLeft(), store),
					normaliseIntegerTerm(((Product)integerTerm).getRight(), store), 
					intermediateVariable);
			store.impose(productConstraint);
			return intermediateVariable;
		} else {
			throw new IllegalArgumentException("Unknown integer term type " + integerTerm.getClass().getName());
		}
		
	}

	private static boolean isInteger(Term term) {
		if (term instanceof IntConstant) {
			return true;
		} else if (term instanceof NumericVariable) {
			return ((NumericVariable)term).isInteger();
		} else if (term instanceof Sum) {
			return isInteger( ((Sum)term).getLeft() ) && 
					isInteger( ((Sum)term).getRight() );
		} else if (term instanceof Product) {
			return isInteger( ((Product)term).getLeft() ) && 
					isInteger( ((Product)term).getRight() );
		}
		throw new IllegalArgumentException("Unknown term type " + term.getClass().getName());
	}
	
}
