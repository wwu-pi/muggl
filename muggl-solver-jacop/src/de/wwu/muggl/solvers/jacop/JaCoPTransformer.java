package de.wwu.muggl.solvers.jacop;

import java.util.ArrayList;

import org.jacop.constraints.Constraint;
import org.jacop.constraints.SumInt;
import org.jacop.constraints.SumWeight;
import org.jacop.constraints.XeqC;
import org.jacop.constraints.XeqY;
import org.jacop.constraints.XgtC;
import org.jacop.constraints.XgtY;
import org.jacop.constraints.XgteqC;
import org.jacop.constraints.XgteqY;
import org.jacop.constraints.XltC;
import org.jacop.constraints.XltY;
import org.jacop.constraints.XlteqC;
import org.jacop.constraints.XlteqY;
import org.jacop.constraints.XmulCeqZ;
import org.jacop.constraints.XmulYeqZ;
import org.jacop.constraints.XneqC;
import org.jacop.constraints.XneqY;
import org.jacop.core.BoundDomain;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.floats.constraints.PeqC;
import org.jacop.floats.constraints.PeqQ;
import org.jacop.floats.constraints.PgtC;
import org.jacop.floats.constraints.PgtQ;
import org.jacop.floats.constraints.PgteqC;
import org.jacop.floats.constraints.PgteqQ;
import org.jacop.floats.constraints.PltC;
import org.jacop.floats.constraints.PltQ;
import org.jacop.floats.constraints.PlteqC;
import org.jacop.floats.constraints.PlteqQ;
import org.jacop.floats.constraints.PneqC;
import org.jacop.floats.constraints.PneqQ;
import org.jacop.floats.constraints.XeqP;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatIntervalDomain;
import org.jacop.floats.core.FloatVar;

import de.wwu.testtool.expressions.TypeCast;

import de.wwu.testtool.expressions.BinaryOperation;
import de.wwu.testtool.expressions.ConstraintExpression;
import de.wwu.testtool.expressions.Difference;
import de.wwu.testtool.expressions.GreaterOrEqual;
import de.wwu.testtool.expressions.GreaterThan;
import de.wwu.testtool.expressions.HasLeftAndRightTerms;
import de.wwu.testtool.expressions.IntConstant;
import de.wwu.testtool.expressions.LessOrEqual;
import de.wwu.testtool.expressions.LessThan;
import de.wwu.testtool.expressions.NumericConstant;
import de.wwu.testtool.expressions.NumericEqual;
import de.wwu.testtool.expressions.NumericNotEqual;
import de.wwu.testtool.expressions.NumericVariable;
import de.wwu.testtool.expressions.Product;
import de.wwu.testtool.expressions.Sum;
import de.wwu.testtool.expressions.Term;
import de.wwu.testtool.expressions.Variable;

public class JaCoPTransformer {
	private static final int DOMAIN_INTEGER_DEPRECIATION =
			10000;
	
	private static final IntDomain DOMAIN_INTEGER = 
			new BoundDomain(IntDomain.MinInt/DOMAIN_INTEGER_DEPRECIATION,
					IntDomain.MaxInt/DOMAIN_INTEGER_DEPRECIATION);

	private static final FloatDomain DOMAIN_FLOAT = 
			new FloatIntervalDomain(FloatDomain.MinFloat, FloatDomain.MaxFloat);

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
				// {{ Integer and constant
				int constantValue = left.isConstant() ? ((NumericConstant)left).getIntValue() : ((NumericConstant)right).getIntValue();
				Term variableTerm = left.isConstant() ? right : left;
				
				// Decide whether sides will need to be switched, as constants may only appear as the right term in JaCoP.
				// This implies mirroring the comparison for lt(e)/gt(e)
				boolean switchSides = left.isConstant();

				IntVar termVar = normaliseIntegerTerm(variableTerm, store);
				// assert: termVar == variableTerm
				
				if (ce instanceof LessOrEqual) {
					if (switchSides) { 
						resultingConstraint = new XgteqC(termVar, constantValue);
					} else {
						resultingConstraint = new XlteqC(termVar, constantValue);
					}
				} else if (ce instanceof LessThan) {
					if (switchSides) { 
						resultingConstraint = new XgtC(termVar, constantValue);
					} else {
						resultingConstraint = new XltC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterOrEqual) {
					if (switchSides) { 
						resultingConstraint = new XlteqC(termVar, constantValue);
					} else {
						resultingConstraint = new XgteqC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterThan) {
					if (switchSides) { 
						resultingConstraint = new XltC(termVar, constantValue);
					} else {
						resultingConstraint = new XgtC(termVar, constantValue);
					}
				} else if (ce instanceof NumericEqual) {
					resultingConstraint = new XeqC(termVar, constantValue);
				} else if (ce instanceof NumericNotEqual) {
					resultingConstraint = new XneqC(termVar, constantValue);
				} else {
					// other type of equation with constant
					throw new IllegalArgumentException("Unknown (with-constant, all-int) constraint type " + ce.getClass().getName());
				}
				// }}
			} else {
				// {{ no constant, all int
				
				IntVar leftTermVar = normaliseIntegerTerm(ce.getLeft(), store);
				IntVar rightTermVar = normaliseIntegerTerm(ce.getRight(), store);
				
				if (ce instanceof NumericEqual) {
					resultingConstraint = new XeqY(leftTermVar, rightTermVar);
				} else if (ce instanceof NumericNotEqual) {
					resultingConstraint = new XneqY(leftTermVar, rightTermVar);
				} else if (ce instanceof LessThan) {
					resultingConstraint = new XltY(leftTermVar, rightTermVar);
				} else if (ce instanceof LessOrEqual) {
					resultingConstraint = new XlteqY(leftTermVar, rightTermVar);
				} else if (ce instanceof GreaterThan) {
					resultingConstraint = new XgtY(leftTermVar, rightTermVar);
				} else if (ce instanceof GreaterOrEqual) {
					resultingConstraint = new XgteqY(leftTermVar, rightTermVar);
				} else {
					throw new IllegalArgumentException("Unknown (non-constant, all-int) constraint type " + ce.getClass().getName());
				}
				// }}
			}
			
		} else {
			// At least one variable is a float variable.
			if (hasConstant) {
				// {{ Float and constant
				double constantValue = left.isConstant() ? ((NumericConstant)left).getDoubleValue() : ((NumericConstant)right).getDoubleValue();
				Term variableTerm = left.isConstant() ? right : left;
				
				// Decide whether sides will need to be switched, as constants may only appear as the right term in JaCoP.
				// This implies mirroring the comparison for lt(e)/gt(e)
				boolean switchSides = left.isConstant();

				FloatVar termVar;
				if (isInteger(ce.getLeft())) {
					IntVar termInt = normaliseIntegerTerm(ce.getLeft(), store);
					termVar = new FloatVar(store, DOMAIN_FLOAT);
					store.impose( new XeqP(termInt, termVar) );
				} else {
					termVar = normaliseFloatTerm(ce.getLeft(), store);
				}
				
				termVar = normaliseFloatTerm(variableTerm, store);
				// assert: termVar == variableTerm

				if (ce instanceof LessOrEqual) {
					if (switchSides) { 
						resultingConstraint = new PgteqC(termVar, constantValue);
					} else {
						resultingConstraint = new PlteqC(termVar, constantValue);
					}
				} else if (ce instanceof LessThan) {
					if (switchSides) { 
						resultingConstraint = new PgtC(termVar, constantValue);
					} else {
						resultingConstraint = new PltC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterOrEqual) {
					if (switchSides) { 
						resultingConstraint = new PlteqC(termVar, constantValue);
					} else {
						resultingConstraint = new PgteqC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterThan) {
					if (switchSides) { 
						resultingConstraint = new PltC(termVar, constantValue);
					} else {
						resultingConstraint = new PgtC(termVar, constantValue);
					}
				} else if (ce instanceof NumericEqual) {
					resultingConstraint = new PeqC(termVar, constantValue);
				} else if (ce instanceof NumericNotEqual) {
					resultingConstraint = new PneqC(termVar, constantValue);
				} else {
					// other type of equation with constant
					throw new IllegalArgumentException("Unknown (with-constant, floating-point) constraint type " + ce.getClass().getName());
				}
				// }}
			} else {
				// {{ no constant, with float

				FloatVar leftTermVar;
				FloatVar rightTermVar; 
				if (isInteger(ce.getLeft())) {
					IntVar leftTermInt = normaliseIntegerTerm(ce.getLeft(), store);
					leftTermVar = new FloatVar(store, DOMAIN_FLOAT);
					store.impose( new XeqP(leftTermInt, leftTermVar) );
				} else {
					leftTermVar = normaliseFloatTerm(ce.getLeft(), store);
				}
				if (isInteger(ce.getRight())) {
					IntVar rightTermInt = normaliseIntegerTerm(ce.getRight(), store);
					rightTermVar = new FloatVar(store, DOMAIN_FLOAT);
					store.impose( new XeqP(rightTermInt, rightTermVar) );
				} else {
					rightTermVar = normaliseFloatTerm(ce.getRight(), store);
				}
				
				if (ce instanceof NumericEqual) {
					resultingConstraint = new PeqQ(leftTermVar, rightTermVar);
				} else if (ce instanceof NumericNotEqual) {
					resultingConstraint = new PneqQ(leftTermVar, rightTermVar);
				} else if (ce instanceof LessThan) {
					resultingConstraint = new PltQ(leftTermVar, rightTermVar);
				} else if (ce instanceof LessOrEqual) {
					resultingConstraint = new PlteqQ(leftTermVar, rightTermVar);
				} else if (ce instanceof GreaterThan) {
					resultingConstraint = new PgtQ(leftTermVar, rightTermVar);
				} else if (ce instanceof GreaterOrEqual) {
					resultingConstraint = new PgteqQ(leftTermVar, rightTermVar);
				} else {
					throw new IllegalArgumentException("Unknown (non-constant, floating-point) constraint type " + ce.getClass().getName());
				}
				// }}
			}
		}
		
		store.impose(resultingConstraint);
		
	}

	private static FloatVar normaliseFloatTerm(Term floatTerm,
			JacopMugglStore store) {
		throw new IllegalArgumentException("Unknown float term type " + floatTerm.getClass().getName());
	}

	private static IntVar normaliseIntegerTerm(Term integerTerm, JacopMugglStore store) {
		if (integerTerm instanceof NumericConstant) {
			// for a constant, just add a variable with a very restricted domain. Should save one constraint.
			int constantValue = ((NumericConstant)integerTerm).getIntValue();
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
		} else if (integerTerm instanceof Sum || integerTerm instanceof Difference) {
			return normaliseIntegerSumOrDifference((BinaryOperation)integerTerm, store);
		} else if (integerTerm instanceof Product) {
			return normaliseIntegerProduct((Product)integerTerm, store);
		} else {
			throw new IllegalArgumentException("Unknown integer term type " + integerTerm.getClass().getName());
		}
		
	}

	/**
	 * @param integerProduct
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
	private static IntVar normaliseIntegerSumOrDifference(BinaryOperation integerSum,
			JacopMugglStore store) {
		// TODO faster handling of simple sums (XplusYeqZ)
		//IntVar intermediateVariable = new IntVar(store, DOMAIN_INTEGER);
		//XplusYeqZ sumConstraint = new XplusYeqZ(
		//		normaliseIntegerTerm(integerSum.getLeft(), store), 
		//		normaliseIntegerTerm(integerSum.getRight(), store), 
		//		intermediateVariable);
		//store.impose(sumConstraint);
		//return intermediateVariable;		
		
		ArrayList<IntVar> termList = new ArrayList<IntVar>();
		ArrayList<Integer> weightList = new ArrayList<Integer>();
		boolean allWeightsOne = true;
		
		allWeightsOne = normaliseIntegerSumRecursive(integerSum, store, termList,
				weightList, false); // false, since the entire term is treated as positive for now.

		// Compose and impose Sum constraint
		IntVar intermediateVariable = new IntVar(store, DOMAIN_INTEGER);
		Constraint sumConstraint;
		if (allWeightsOne) {
			sumConstraint = new SumInt(store, termList, "==", intermediateVariable);
		} else {
			sumConstraint = new SumWeight(termList, weightList, intermediateVariable);
		}

		System.out.println(sumConstraint.toString());
		System.out.println(weightList.toString());
		
		store.impose(sumConstraint);
		return intermediateVariable;
		
	}

	/**
	 * @param integerSum
	 * @param store
	 * @param termList
	 * @param weightList
	 * @param negate True if the entire term is to be treated as negative (e.g. right hand side of a difference) 
	 * @return
	 */
	private static boolean normaliseIntegerSumRecursive(BinaryOperation integerSum,
			JacopMugglStore store, ArrayList<IntVar> termList,
			ArrayList<Integer> weightList, boolean negate) {
		boolean leftAllWeightsEqOne;
		boolean rightAllWeightsEqOne;
		
		if (integerSum.getLeft() instanceof Sum ||
				integerSum.getLeft() instanceof Difference) {
			leftAllWeightsEqOne = normaliseIntegerSumRecursive((BinaryOperation) integerSum.getLeft(), store, termList, weightList, negate);
		} else {
			leftAllWeightsEqOne = processTermOrSimpleProductAndTestWeightEqOne(store, 
					termList, weightList, integerSum.getLeft(), negate); //TODO negation
		}
		
		// Right hand side must be negative if EITHER entire term is negated XOR current term is a difference.
		boolean negateRHS = negate ^ (integerSum instanceof Difference);
		
		if (integerSum.getRight() instanceof Sum ||
				integerSum.getRight() instanceof Difference) {
			rightAllWeightsEqOne = normaliseIntegerSumRecursive((BinaryOperation) integerSum.getRight(), store, termList, weightList, negateRHS);
		} else {
			rightAllWeightsEqOne = processTermOrSimpleProductAndTestWeightEqOne(store, 
					termList, weightList, integerSum.getRight(), negateRHS); //TODO negation
		}
		
		return leftAllWeightsEqOne && rightAllWeightsEqOne;
	}

	private static boolean processTermOrSimpleProductAndTestWeightEqOne(JacopMugglStore store,
			ArrayList<IntVar> termList, ArrayList<Integer> weightList, Term term, boolean negate) {
		
		int weight;
		
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
			
			termList.add( normaliseIntegerTerm(variableTerm, store) );
			
			weight = ((IntConstant)constantTerm).getIntValue();
			if (negate) { 
				weight = -weight; 
			}
			weightList.add( weight );
		} else {
			termList.add( normaliseIntegerTerm(term, store) );
			
			weight = negate ? -1 : 1;
			weightList.add( weight );
		}
		
		return weight == 1;
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
		} else if (term instanceof BinaryOperation) {
			return isInteger( ((BinaryOperation)term).getLeft() ) && 
					isInteger( ((BinaryOperation)term).getRight() );
		} else if (term instanceof TypeCast) {
			return Term.isIntegerType(((TypeCast)term).getType());
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
		java.lang.reflect.Field f;
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
