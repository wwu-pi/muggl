package de.wwu.muggl.solvers.jacop;

import java.util.ArrayList;

import org.jacop.constraints.*;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.IntervalDomain;
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
import org.jacop.floats.constraints.PminusCeqR;
import org.jacop.floats.constraints.PminusQeqR;
import org.jacop.floats.constraints.PmulCeqR;
import org.jacop.floats.constraints.PmulQeqR;
import org.jacop.floats.constraints.PneqC;
import org.jacop.floats.constraints.PneqQ;
import org.jacop.floats.constraints.PplusCeqR;
import org.jacop.floats.constraints.PplusQeqR;
import org.jacop.floats.constraints.XeqP;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatIntervalDomain;
import org.jacop.floats.core.FloatVar;

import de.wwu.muggl.solvers.expressions.TypeCast;
import de.wwu.muggl.solvers.expressions.BinaryOperation;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.Difference;
import de.wwu.muggl.solvers.expressions.DoubleConstant;
import de.wwu.muggl.solvers.expressions.FloatConstant;
import de.wwu.muggl.solvers.expressions.GreaterOrEqual;
import de.wwu.muggl.solvers.expressions.GreaterThan;
import de.wwu.muggl.solvers.expressions.HasLeftAndRightTerms;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.LessOrEqual;
import de.wwu.muggl.solvers.expressions.LessThan;
import de.wwu.muggl.solvers.expressions.LongConstant;
import de.wwu.muggl.solvers.expressions.Modulo;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericEqual;
import de.wwu.muggl.solvers.expressions.NumericNotEqual;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Or;
import de.wwu.muggl.solvers.expressions.Product;
import de.wwu.muggl.solvers.expressions.Sum;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.solvers.expressions.Variable;

/**
 * JaCoPTransformer
 * 
 * Provides static transformation routines,
 * manipulating the state of a JaCoPMugglStore.
 * Entrypoint: {@link JaCoPTransformer#transformAndImpose(ConstraintExpression, JacopMugglStore)}.
 * 
 * @author Jan C. Dageförde, 2015.
 *
 */
public class JaCoPTransformer {
	private static final int DOMAIN_DEPRECIATION =
			10_000;

    //private static final IntDomain DOMAIN_INTEGER =
    //        new BoundDomain(IntDomain.MinInt/DOMAIN_DEPRECIATION,
    //                IntDomain.MaxInt/DOMAIN_DEPRECIATION);

    private static final IntDomain DOMAIN_INTEGER =
            new IntervalDomain(IntDomain.MinInt/DOMAIN_DEPRECIATION,
                    IntDomain.MaxInt/DOMAIN_DEPRECIATION);

	private static final FloatDomain DOMAIN_FLOAT = 
			new FloatIntervalDomain(FloatDomain.MinFloat/DOMAIN_DEPRECIATION, 
					FloatDomain.MaxFloat/DOMAIN_DEPRECIATION);

	public static void transformAndImpose(ConstraintExpression ce, JacopMugglStore store) {
		
		if (ce instanceof HasLeftAndRightTerms) {
			imposeEquation((HasLeftAndRightTerms) ce, store);
		} else if (ce instanceof Or) {
			imposeDisjunction((Or)ce, store);
		} else {
			throw new IllegalArgumentException("Unknown constraint type " + ce.getClass().getName());
		}
	}

	private static void imposeDisjunction(Or ce, JacopMugglStore store) {
		ConstraintExpression e1 = ce.getE1();
		ConstraintExpression e2 = ce.getE1();

		PrimitiveConstraint consE1 = generateEquationConstraint((HasLeftAndRightTerms) e1, store);
		PrimitiveConstraint consE2 = generateEquationConstraint((HasLeftAndRightTerms) e2, store);
		
		org.jacop.constraints.Or resultingConstraint = new org.jacop.constraints.Or(consE1, consE2);
		store.impose(resultingConstraint);
	}

	private static void imposeEquation(HasLeftAndRightTerms ce, JacopMugglStore store) {
		//System.out.println("Transforming equation " + ce);
		Term left = ce.getLeft();
		Term right = ce.getRight();
		
		if (left.isConstant() && right.isConstant()) { // sanity check (AND no JaCoP constraint would exist for this)
			throw new IllegalStateException("Both terms of the equation " + ce + " are constant. Why was this constraint added in the first place?");
		}

		Constraint resultingConstraint = generateEquationConstraint(ce, store);
		
		store.impose(resultingConstraint);
		
	}

	/**
	 * @param ce
	 * @param store
	 * @return
	 */
	private static PrimitiveConstraint generateEquationConstraint(
			HasLeftAndRightTerms ce, JacopMugglStore store) {
		Term left = ce.getLeft();
		Term right = ce.getRight();
		
		boolean hasConstant = left.isConstant() || right.isConstant();
		boolean isInteger = isInteger(left) && isInteger(right);
		
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
						return new XgteqC(termVar, constantValue);
					} else {
						return new XlteqC(termVar, constantValue);
					}
				} else if (ce instanceof LessThan) {
					if (switchSides) { 
						return new XgtC(termVar, constantValue);
					} else {
						return new XltC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterOrEqual) {
					if (switchSides) { 
						return new XlteqC(termVar, constantValue);
					} else {
						return new XgteqC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterThan) {
					if (switchSides) { 
						return new XltC(termVar, constantValue);
					} else {
						return new XgtC(termVar, constantValue);
					}
				} else if (ce instanceof NumericEqual) {
					return new XeqC(termVar, constantValue);
				} else if (ce instanceof NumericNotEqual) {
					return new XneqC(termVar, constantValue);
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
					return new XeqY(leftTermVar, rightTermVar);
				} else if (ce instanceof NumericNotEqual) {
					return new XneqY(leftTermVar, rightTermVar);
				} else if (ce instanceof LessThan) {
					return new XltY(leftTermVar, rightTermVar);
				} else if (ce instanceof LessOrEqual) {
					return new XlteqY(leftTermVar, rightTermVar);
				} else if (ce instanceof GreaterThan) {
					return new XgtY(leftTermVar, rightTermVar);
				} else if (ce instanceof GreaterOrEqual) {
					return new XgteqY(leftTermVar, rightTermVar);
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
				if (isInteger(variableTerm)) {
					IntVar termInt = normaliseIntegerTerm(variableTerm, store);
					termVar = new FloatVar(store, DOMAIN_FLOAT);
					store.impose( new XeqP(termInt, termVar) );
				} else {
					termVar = normaliseFloatTerm(variableTerm, store);
				}
				
				if (ce instanceof LessOrEqual) {
					if (switchSides) { 
						return new PgteqC(termVar, constantValue);
					} else {
						return new PlteqC(termVar, constantValue);
					}
				} else if (ce instanceof LessThan) {
					if (switchSides) { 
						return new PgtC(termVar, constantValue);
					} else {
						return new PltC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterOrEqual) {
					if (switchSides) { 
						return new PlteqC(termVar, constantValue);
					} else {
						return new PgteqC(termVar, constantValue);
					}
				} else if (ce instanceof GreaterThan) {
					if (switchSides) { 
						return new PltC(termVar, constantValue);
					} else {
						return new PgtC(termVar, constantValue);
					}
				} else if (ce instanceof NumericEqual) {
					return new PeqC(termVar, constantValue);
				} else if (ce instanceof NumericNotEqual) {
					return new PneqC(termVar, constantValue);
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
					return new PeqQ(leftTermVar, rightTermVar);
				} else if (ce instanceof NumericNotEqual) {
					return new PneqQ(leftTermVar, rightTermVar);
				} else if (ce instanceof LessThan) {
					return new PltQ(leftTermVar, rightTermVar);
				} else if (ce instanceof LessOrEqual) {
					return new PlteqQ(leftTermVar, rightTermVar);
				} else if (ce instanceof GreaterThan) {
					return new PgtQ(leftTermVar, rightTermVar);
				} else if (ce instanceof GreaterOrEqual) {
					return new PgteqQ(leftTermVar, rightTermVar);
				} else {
					throw new IllegalArgumentException("Unknown (non-constant, floating-point) constraint type " + ce.getClass().getName());
				}
				// }}
			}
		}
	}

	private static FloatVar normaliseFloatTerm(Term floatTerm,
			JacopMugglStore store) {
		if (floatTerm instanceof NumericConstant) {
			// for a constant, just add a variable with a very restricted domain. Should save one constraint.
			double constantValue = ((NumericConstant)floatTerm).getDoubleValue();
			return new FloatVar(store, constantValue, constantValue);
		} else if (floatTerm instanceof NumericVariable) {
			FloatVar floatVar = (FloatVar) store.getVariable((Variable)floatTerm);
			
			if (floatVar == null) {
				floatVar = new FloatVar(store, 
					((NumericVariable)floatTerm).getInternalName(), 
					DOMAIN_FLOAT);
				// add to cache
				store.addVariable((Variable)floatTerm, floatVar);
			}
			return floatVar;
		} else if (floatTerm instanceof TypeCast) {
			TypeCast cast = (TypeCast)floatTerm;
			// implicit: this casts from integer to float
			
			IntVar original = normaliseIntegerTerm(cast.getInternalTerm(), store);
			FloatVar casted = new FloatVar(store, DOMAIN_FLOAT);
			store.impose(new XeqP(original, casted));
			return casted;
		} else if (floatTerm instanceof Sum) {
			Sum floatSum = (Sum) floatTerm;
			if (floatSum.getLeft().isConstant() || floatSum.getRight().isConstant()) {
				// one term is constant
				double constantValue = floatSum.getLeft().isConstant() ? ((NumericConstant)floatSum.getLeft()).getDoubleValue() : ((NumericConstant)floatSum.getRight()).getDoubleValue();
				Term variableTerm = floatSum.getLeft().isConstant() ? floatSum.getRight() : floatSum.getLeft();
				FloatVar floatVar = new FloatVar(store, DOMAIN_FLOAT);
				FloatVar variableJaCoP = normaliseFloatTerm(variableTerm, store);
				store.impose(
						new PplusCeqR(variableJaCoP, constantValue, 
								floatVar)
						);
				return floatVar;
				
			} else {
				// no term is constant
				FloatVar floatVar = new FloatVar(store, DOMAIN_FLOAT);
				FloatVar lhs = normaliseFloatTerm(floatSum.getLeft(), store);
				FloatVar rhs = normaliseFloatTerm(floatSum.getRight(), store);
				store.impose(
						new PplusQeqR(lhs, rhs, 
								floatVar)
						);
				return floatVar;
			}
		} else if (floatTerm instanceof Difference) {
			Difference floatDifference = (Difference) floatTerm;

			if (floatDifference.getRight().isConstant()) {
				// right term is constant
				// Note: Only right may be constant, as there is only P-C=R -- no C-P=R or T+P=C!
				// For achieving T-C=-P (only valid form of the equation that has a constant at second place),
				// an additional constraint would be required to obtain -P.
				// Consequently, this is not treated individually in order to avoid an additional special case.
				double rhs = ((NumericConstant)floatDifference.getRight()).getDoubleValue();
				Term lhs = floatDifference.getLeft();
				FloatVar floatVar = new FloatVar(store, DOMAIN_FLOAT);
				FloatVar variableJaCoP = normaliseFloatTerm(lhs, store);
				store.impose(
						new PminusCeqR(variableJaCoP, rhs, 
								floatVar)
						);
				return floatVar;
				
			} else {
				// no term or left term is constant
				FloatVar floatVar = new FloatVar(store, DOMAIN_FLOAT);
				FloatVar lhs = normaliseFloatTerm(floatDifference.getLeft(), store);
				FloatVar rhs = normaliseFloatTerm(floatDifference.getRight(), store);
				store.impose(
						new PminusQeqR(lhs, rhs, 
								floatVar)
						);
				return floatVar;
			}
		} else if (floatTerm instanceof Product) {
			Product floatProduct = (Product) floatTerm;
			if (floatProduct.getLeft().isConstant() || floatProduct.getRight().isConstant()) {
				// one term is constant
				double constantValue = floatProduct.getLeft().isConstant() ? ((NumericConstant)floatProduct.getLeft()).getDoubleValue() : ((NumericConstant)floatProduct.getRight()).getDoubleValue();
				Term variableTerm = floatProduct.getLeft().isConstant() ? floatProduct.getRight() : floatProduct.getLeft();
				FloatVar floatVar = new FloatVar(store, DOMAIN_FLOAT);
				FloatVar variableJaCoP = normaliseFloatTerm(variableTerm, store);
				store.impose(
						new PmulCeqR(variableJaCoP, constantValue, 
								floatVar)
						);
				return floatVar;
				
			} else {
				// no term is constant
				FloatVar floatVar = new FloatVar(store, DOMAIN_FLOAT);
				FloatVar lhs = normaliseFloatTerm(floatProduct.getLeft(), store);
				FloatVar rhs = normaliseFloatTerm(floatProduct.getRight(), store);
				store.impose(
						new PmulQeqR(lhs, rhs, 
								floatVar)
						);
				return floatVar;
			}
		}
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
					((NumericVariable)integerTerm).getName() + "_" + integerTerm.hashCode(),
					DOMAIN_INTEGER);
				// add to cache
				store.addVariable((Variable)integerTerm, intVar);
			}
			return intVar;
		} else if (integerTerm instanceof TypeCast) {
			TypeCast cast = (TypeCast)integerTerm;
			// implicit: this casts from float to integer
			
			FloatVar original = normaliseFloatTerm(cast.getInternalTerm(), store);
			IntVar casted = new IntVar(store, DOMAIN_INTEGER);
			store.impose(new XeqP(casted, original));
			return casted;
		} else if (integerTerm instanceof Sum || integerTerm instanceof Difference) {
			return normaliseIntegerSumOrDifference((BinaryOperation)integerTerm, store);
		} else if (integerTerm instanceof Product) {
			return normaliseIntegerProduct((Product)integerTerm, store);
		} else if (integerTerm instanceof Modulo) {
			IntVar lhs = normaliseIntegerTerm(((Modulo)integerTerm).getLeft(), store);
			IntVar rhs = normaliseIntegerTerm(((Modulo)integerTerm).getRight(), store);
			IntVar result = new IntVar(store, DOMAIN_INTEGER);
			store.impose(new XmodYeqZ(lhs, rhs, result));
			return result;
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
		
		ArrayList<IntVar> termList = new ArrayList<IntVar>();
		ArrayList<Integer> weightList = new ArrayList<Integer>();
		boolean allWeightsOne = true;
		
		allWeightsOne = normaliseIntegerSumRecursive(integerSum, store, termList,
				weightList, false); // false, since the entire term is treated as positive for now.

		// Compose and impose Sum constraint
		IntVar intermediateVariable = new IntVar(store, DOMAIN_INTEGER);
		Constraint sumConstraint;
		if (allWeightsOne) {
			sumConstraint = new SumInt(termList, "==", intermediateVariable);
		} else {
            //sumConstraint = new SumWeight(termList, weightList, intermediateVariable);
		    int[] weights = new int[weightList.size()];
		    for (int i = 0; i < weights.length; i++) {
		        weights[i] = weightList.get(i);
            }
			sumConstraint = new LinearInt(termList.toArray(new IntVar[]{}), weights, "==", intermediateVariable);
		}

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
			leftAllWeightsEqOne = processIntegerTermOrSimpleProductAndTestWeightEqOne(store, 
					termList, weightList, integerSum.getLeft(), negate);
		}
		
		// Right hand side must be negative if EITHER entire term is negated XOR current term is a difference.
		boolean negateRHS = negate ^ (integerSum instanceof Difference);
		
		if (integerSum.getRight() instanceof Sum ||
				integerSum.getRight() instanceof Difference) {
			rightAllWeightsEqOne = normaliseIntegerSumRecursive((BinaryOperation) integerSum.getRight(), store, termList, weightList, negateRHS);
		} else {
			rightAllWeightsEqOne = processIntegerTermOrSimpleProductAndTestWeightEqOne(store, 
					termList, weightList, integerSum.getRight(), negateRHS);
		}
		
		return leftAllWeightsEqOne && rightAllWeightsEqOne;
	}

	private static boolean processIntegerTermOrSimpleProductAndTestWeightEqOne(JacopMugglStore store,
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
		if (term instanceof IntConstant || term instanceof LongConstant) {
			return true;
		} else if (term instanceof DoubleConstant || term instanceof FloatConstant) {
			return false;
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
	 * DEBUGGING HELPER METHOD.
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
			e.printStackTrace();
			return Integer.MAX_VALUE;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}
	}
	
}
