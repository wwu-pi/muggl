package de.wwu.muggl.solvers.jacop;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;

import org.jacop.constraints.Constraint;
import org.jacop.constraints.SumInt;
import org.jacop.constraints.SumWeight;
import org.jacop.constraints.XeqC;
import org.jacop.constraints.XeqY;
import org.jacop.constraints.XgtC;
import org.jacop.constraints.XgteqC;
import org.jacop.constraints.XltC;
import org.jacop.constraints.XlteqC;
import org.jacop.constraints.XmulCeqZ;
import org.jacop.constraints.XmulYeqZ;
import org.jacop.constraints.XneqC;
import org.jacop.constraints.XneqY;
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
	private static final IntervalDomain DOMAIN_INTEGER = new IntervalDomain(-2, 13299);

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
		
		boolean hasConstant = left.isConstant() || right.isConstant();
		boolean isInteger = isInteger(left) && isInteger(right);
		
		Constraint resultingConstraint;
		
		if (isInteger) {
			if (hasConstant) {
				int constantValue = left.isConstant() ? ((IntConstant)left).getValue() : ((IntConstant)right).getValue();
				Term variableTerm = left.isConstant() ? right : left;
				
				// Decide whether sides will need to be switched, as constants may only appear as the right term in JaCoP.
				// This implies mirroring the comparison for lt(e)/gt(e)
				boolean switchSides = left.isConstant();

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
				} else if (ce instanceof GreaterOrEqual) {
					IntVar termVar = normaliseIntegerTerm(variableTerm, store);
					// assert: termVar == variableTerm
					
					if (switchSides) { 
						resultingConstraint = new XlteqC(termVar, constantValue);
					} else {
						resultingConstraint = new XgteqC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterThan) {
					IntVar termVar = normaliseIntegerTerm(variableTerm, store);
					// assert: termVar == variableTerm
					
					if (switchSides) { 
						resultingConstraint = new XltC(termVar, constantValue);
					} else {
						resultingConstraint = new XgtC(termVar, constantValue);
					}
				} else if (ce instanceof NumericEqual) {
					IntVar termVar = normaliseIntegerTerm(variableTerm, store);
					// assert: termVar == variableTerm
					
					resultingConstraint = new XeqC(termVar, constantValue);
				} else if (ce instanceof NumericNotEqual) {
					IntVar termVar = normaliseIntegerTerm(variableTerm, store);
					// assert: termVar == variableTerm
					
					resultingConstraint = new XneqC(termVar, constantValue);
				} else {
					// other type of equation with constant
					throw new IllegalArgumentException("Unknown (with-constant, all-int) constraint type " + ce.getClass().getName());
				}
			} else {
				// no constant, all int
				
				// Decide whether constraints need to be normalised to lt(e) constraints.
				//boolean switchSides = ce instanceof GreaterOrEqual || ce instanceof GreaterThan;
				
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
			return normaliseIntegerSum((Sum)integerTerm, store);
		} else if (integerTerm instanceof Product) {
			return normaliseIntegerProduct((Product)integerTerm, store);
		} else {
			throw new IllegalArgumentException("Unknown integer term type " + integerTerm.getClass().getName());
		}
		
	}

	/**
	 * @param integerTerm
	 * @param store
	 * @return
	 */
	private static IntVar normaliseIntegerProduct(Product integerProduct,
			JacopMugglStore store) {
		// Returns an intermediate variable, and impose a constraint that this intermediate
		// variable be the product of its two factors. Note that a term may be anything (e.g. a Product),
		// so that this function may work recursively until reaching a constant or variable 
		IntVar intermediateVariable = new IntVar(store, DOMAIN_INTEGER);
		
		Constraint productConstraint;
		
		
		if (integerProduct.getLeft().isConstant() || integerProduct.getRight().isConstant()) {
			int constantValue = integerProduct.getLeft().isConstant() ? ((IntConstant)integerProduct.getLeft()).getValue() : ((IntConstant)integerProduct.getRight()).getValue();
			Term variableTerm = integerProduct.getLeft().isConstant() ? integerProduct.getRight() : integerProduct.getLeft();
			
			productConstraint = new XmulCeqZ(
					normaliseIntegerTerm(variableTerm, store),
					constantValue, 
					intermediateVariable);
		} else {
			productConstraint = new XmulYeqZ(
					normaliseIntegerTerm(integerProduct.getLeft(), store),
					normaliseIntegerTerm(integerProduct.getRight(), store), 
					intermediateVariable);
		}
		store.impose(productConstraint);
		return intermediateVariable;
	}

	/**
	 * Returns an intermediate variable, and impose a constraint that this intermediate
	 * variable be the sum of its two terms. In case one or both terms are again a Sum,
	 * the resulting constraint is composed from all comprised Sums.		
	 * @param integerSum Term that is a sum
	 * @param store JaCoPStore used for storing the resulting constraint
	 * @return One IntVar that is constrained to be equal to the Sum
	 */
	private static IntVar normaliseIntegerSum(Sum integerSum,
			JacopMugglStore store) {
		// TODO faster handling of simple sums (XplusYeqZ)
		//IntVar intermediateVariable = new IntVar(store, DOMAIN_INTEGER);
		//XplusYeqZ sumConstraint = new XplusYeqZ(
		//		normaliseIntegerTerm(integerSum.getLeft(), store), 
		//		normaliseIntegerTerm(integerSum.getRight(), store), 
		//		intermediateVariable);
		//store.impose(sumConstraint);
		//return intermediateVariable;		
		
		ArrayDeque<Sum> pendingSums = new ArrayDeque<Sum>();
		ArrayList<IntVar> termList = new ArrayList<IntVar>();
		ArrayList<Integer> weightList = new ArrayList<Integer>();
		boolean allWeightsOne = true;
		
		pendingSums.addLast(integerSum);
		while (!pendingSums.isEmpty()) {
			Sum currentSum = pendingSums.removeLast();
			if (currentSum.getLeft() instanceof Sum) {
				pendingSums.addLast((Sum)currentSum.getLeft());
			} else {
				allWeightsOne = processTermOrSimpleProductAndTestWeightEqOne(store, 
						termList, weightList, currentSum.getLeft());
			}
			if (currentSum.getRight() instanceof Sum) {
				pendingSums.addLast((Sum)currentSum.getRight());
			} else {
				allWeightsOne = processTermOrSimpleProductAndTestWeightEqOne(store, 
						termList, weightList, currentSum.getRight());
			}
		}

		// Compose and impose Sum constraint
		IntVar intermediateVariable = new IntVar(store, DOMAIN_INTEGER);
		Constraint sumConstraint;
		if (allWeightsOne) {
			sumConstraint = new SumInt(store, termList, "==", intermediateVariable);
		} else {
			sumConstraint = new SumWeight(termList, weightList, intermediateVariable);
		}
		
		store.impose(sumConstraint);
		return intermediateVariable;
		
	}

	private static boolean processTermOrSimpleProductAndTestWeightEqOne(JacopMugglStore store,
			ArrayList<IntVar> termList, ArrayList<Integer> weightList, Term term) {
		if (isSimpleProduct(term)) {
			Product p = (Product)term;
			
			Term constantTerm;
			Term variableTerm;
			
			if (p.getLeft().isConstant()) {
				constantTerm = p.getLeft();
				variableTerm = p.getRight();
			} else {
				constantTerm = p.getRight();
				variableTerm = p.getLeft();
			}
			
			int weight = ((IntConstant)constantTerm).getIntValue();
			
			termList.add( normaliseIntegerTerm(variableTerm, store) );
			weightList.add( weight );
			
			return weight == 1;
		} else {
			termList.add( normaliseIntegerTerm(term, store) );
			weightList.add( 1 );
			return true;
		}
	}

	private static boolean isSimpleProduct(Term term) {
		if (!(term instanceof Product)) {
			return false;
		}
				
		Product p = (Product)term;
		
		boolean leftIsConstant = p.getLeft().isConstant();
		boolean rightIsConstant = p.getRight().isConstant();
		
		if (!leftIsConstant && !rightIsConstant) {
			return false;
		}
		
		return true;
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
	
	/**
	 * Gets ID of current JaCoP DFS instance. Used for conditional breakpoints.
	 * Necessary, because ID value is protected. Therefore, this method uses
	 * reflection to make the ID value visible if needed.
	 * @return ID of current JaCoP DFS instance
	 */
	@SuppressWarnings("unused")
	private static int checkDFSid() {
		Field f;
		try {
			f = org.jacop.search.DepthFirstSearch.class.getDeclaredField("no");
			f.setAccessible(true);
			return f.getInt(null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Integer.MAX_VALUE;
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Integer.MAX_VALUE;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Integer.MAX_VALUE;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}
	}
	
}
