package de.wwu.muggl.solvers.z3;

import com.microsoft.z3.*;
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.solvers.solver.constraints.ArraySelect;
import de.wwu.muggl.solvers.solver.constraints.ArrayStore;
import de.wwu.muggl.vm.initialization.IReferenceValue;

import java.util.*;

/**
 * Z3MugglStore
 * 
 * Specialises the Z3 store to maintain some Muggl-specifc state of the store:
 * Correspondence between variables of the two systems 
 * 
 * @author Jan C. Dageförde, Hendrik Winkelmann, 2020.
 *
 */
public class Z3MugglAdapter {
	// TODO Differentiate exceptions
	private final Context context;
	protected final Solver solver;
	private int level = 0;
	protected final Z3MugglStore valueConnections = new Z3MugglStore();
	protected final Stack<ConstraintExpression> addedConstraints = new Stack<>();

	public Z3MugglAdapter() {
		context = new Context();
		Params parameters = context.mkParams();
		parameters.add("timeout", 20000);
		parameters.add("auto_config", true);
		parameters.add("model", true);
		//context.usingParams(Tactic)
		solver = context.mkSolver();
	}

	public boolean isSatisfiable() {
		Status solverStatus = solver.check();
		if (solverStatus == Status.UNKNOWN) {
			throw new RuntimeException(solver.getReasonUnknown());
		}
		return solverStatus == Status.SATISFIABLE;
	}

	public void imposeConstraint(ConstraintExpression ce) {
		BoolExpr expression = getConstraintExpression(ce);
		solver.add(expression);
	}

	public BoolExpr getConstraintExpression(ConstraintExpression ce) {
		if (ce instanceof Not) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (ce instanceof BooleanEqual) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (ce instanceof BooleanNotEqual) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (ce instanceof SingleConstraintExpression) {
			return getSingleConstraintExpression((SingleConstraintExpression) ce);
		} else if (ce instanceof And) {
			ConstraintExpression lhs = ((And) ce).getE1();
			ConstraintExpression rhs = ((And) ce).getE2();
			return context.mkAnd(getConstraintExpression(lhs), getConstraintExpression(rhs));
		} else if (ce instanceof Or) {
			ConstraintExpression lhs = ((Or) ce).getE1();
			ConstraintExpression rhs = ((Or) ce).getE2();
			return context.mkOr(getConstraintExpression(lhs), getConstraintExpression(rhs));
		} else if (ce instanceof Xor) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (ce instanceof LessThan) {
			return getLT(((LessThan) ce).getLeft(), ((LessThan) ce).getRight());
		} else if (ce instanceof LessOrEqual) {
			return getLTE(((LessOrEqual) ce).getLeft(), ((LessOrEqual) ce).getRight());
		} else if (ce instanceof GreaterThan) {
			return getLT(((GreaterThan) ce).getRight(), ((GreaterThan) ce).getLeft());
		} else if (ce instanceof GreaterOrEqual) {
			return getLTE(((GreaterOrEqual) ce).getRight(), ((GreaterOrEqual) ce).getLeft());
		} else if (ce instanceof NumericEqual || ce instanceof  NumericNotEqual) {
			Term lhs = ((HasLeftAndRightTerms) ce).getLeft();
			Term rhs = ((HasLeftAndRightTerms) ce).getRight();
			// TODO Double equality?
			BoolExpr eq =  context.mkEq(exprFromTerm(lhs), exprFromTerm(rhs));
			if (ce instanceof NumericNotEqual) {
				return context.mkNot(eq);
			} else {
				return eq;
			}
		} else if (ce instanceof ArraySelect) {
			return getArrayAccessExpr((ArraySelect) ce);
		} else if (ce instanceof ArrayStore) {
			return getArrayStoreExpr((ArrayStore) ce);
		} else {
			throw new UnsupportedOperationException("Case not handled: " + ce);
		}
	}

	private BoolExpr getArrayStoreExpr(ArrayStore arrayStore) {
		IReferenceValue arrayref = arrayStore.getArrayref();
		ArrayExpr array = getArrayExprForArrayref(arrayref, arrayStore.getStoredValueTerm(), arrayStore.getName());
		Expr indexExpr = exprFromTerm(arrayStore.getIndexTerm());
		Expr storeValue = exprFromTerm(arrayStore.getStoredValueTerm());
		// Get the storeExpr which is equal to array, except that it has storeValue at indexExpr.
		ArrayExpr storeExpr = context.mkStore(array, indexExpr, storeValue);
		Stack<ArrayExpr> arrayExprsForArrayref = this.arrayrefsToMostRecentArrayExpr.get(arrayref);
		// Save it as the most recent ArrayExpr representing the FreeArray
		arrayExprsForArrayref.push(storeExpr);
		BoolExpr arrayEqual = context.mkEq(storeExpr, storeExpr);
		return arrayEqual;
	}


	private BoolExpr getArrayAccessExpr(ArraySelect arraySelect) {
		IReferenceValue arrayref = arraySelect.getArrayref();
		ArrayExpr array = getArrayExprForArrayref(arrayref, arraySelect.getLoadedValueTerm(), arraySelect.getName());
		Expr indexExpr = exprFromTerm(arraySelect.getIndexTerm());
		Expr selectExpr = context.mkSelect(array, indexExpr);
		Expr loadedValue = exprFromTerm(arraySelect.getLoadedValueTerm());
		BoolExpr equals = context.mkEq(selectExpr, loadedValue);
		return equals;
	}

	private ArrayExpr newArrayExprFromTerm(String varName, Term value) {
		// TODO More than integer should be possible.
		return context.mkArrayConst(varName + "_" + arrayId++, context.mkIntSort(), context.mkIntSort());
	}

	protected int arrayId = 0;

	// For each new ArrayStore-constraint, replace the current ArrayExpr with the new resulting ArrayExpr.
	// For each backtracking-step in which a ArrayStore-constraint is popped, also pop the corresponding ArrayExpr.
	protected final Map<IReferenceValue, Stack<ArrayExpr>> arrayrefsToMostRecentArrayExpr = new HashMap<>();
	private ArrayExpr getArrayExprForArrayref(IReferenceValue arrayref, Term storedValue, String varName) {
		Stack<ArrayExpr> arraysForArrayref = arrayrefsToMostRecentArrayExpr.get(arrayref);
		if (arraysForArrayref == null) {
			arraysForArrayref = new Stack<>();
			arrayrefsToMostRecentArrayExpr.put(arrayref, arraysForArrayref);
			ArrayExpr array = newArrayExprFromTerm(varName, storedValue);
			arraysForArrayref.push(array);
		}
		return arraysForArrayref.peek();
	}

	private BoolExpr getSingleConstraintExpression(SingleConstraintExpression sce) {
		if (sce instanceof BooleanConstant) {
			BooleanConstant temp = (BooleanConstant) sce;
			return context.mkBool(temp.getValue());
		} else if (sce instanceof BooleanVariable) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else {
			throw new UnsupportedOperationException("Case not handled: " + sce);
		}
	}

	private BoolExpr getLT(Term lhs, Term rhs) {
		Expr lhsExpr = exprFromTerm(lhs);
		Expr rhsExpr = exprFromTerm(rhs);
		try {
			return context.mkLt((ArithExpr) lhsExpr, (ArithExpr) rhsExpr);
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		}
	}

	private BoolExpr getLTE(Term lhs, Term rhs) {
		Expr lhsExpr = exprFromTerm(lhs);
		Expr rhsExpr = exprFromTerm(rhs);
		try {
			return context.mkLe((ArithExpr) lhsExpr, (ArithExpr) rhsExpr);
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		}
	}

	private Expr exprFromTerm(Term t) {
		if (t instanceof Negation) {
			return context.mkNot((BoolExpr) exprFromTerm(((Negation) t).getExpr()));
		} else if (t instanceof NumericVariable) {
			return numericVariableFromTerm((NumericVariable) t);
		} else if (t instanceof NumericConstant) {
			return numericConstantFromTerm((NumericConstant) t);
		} else if (t instanceof BinaryOperation) {
			return exprFromBinaryOperation((BinaryOperation) t);
		} else if (t instanceof ObjectExpression) {
			throw new UnsupportedOperationException("Case not handled: " + t);
		} else {
			throw new UnsupportedOperationException("Case not handled: " + t);
		}
	}

	private Expr numericConstantFromTerm(NumericConstant nc) {
		if (nc.isInteger()) {
			return context.mkInt(nc.getLongValue());
		} else {
			return context.mkReal(String.valueOf(nc.getDoubleValue()));
		}
	}

	private Expr numericVariableFromTerm(NumericVariable nv) {
		Expr result = valueConnections.getVariable(nv);
		if (result != null) {
			return result;
		}
		if (nv.isInteger()) {
			result = context.mkIntConst(nv.getName() + "_" + nv.getInternalName());
		} else {
			result = context.mkRealConst(nv.getName() + "_" + nv.getInternalName());
		}
		valueConnections.addVariable(nv, result);
		return result;
	}

	private Expr exprFromBinaryOperation(BinaryOperation bo) {
		if (bo instanceof Quotient) {
			ArithExpr numExpr = (ArithExpr) exprFromTerm(((Quotient) bo).getNumerator());
			ArithExpr denExpr = (ArithExpr) exprFromTerm(((Quotient) bo).getDenominator());
			return context.mkDiv(numExpr, denExpr);
		} else if (bo instanceof Modulo) {
			IntExpr lhsExpr = (IntExpr) exprFromTerm(bo.getLeft());
			IntExpr rhsExpr = (IntExpr) exprFromTerm(bo.getRight());
			return context.mkMod(lhsExpr, rhsExpr);
		} else if (bo instanceof Product) {
			ArithExpr lhsExpr = (ArithExpr) exprFromTerm(bo.getLeft());
			ArithExpr rhsExpr = (ArithExpr) exprFromTerm(bo.getRight());
			return context.mkMul(lhsExpr, rhsExpr);
		} else if (bo instanceof ArithmeticShiftRight) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (bo instanceof ArithmeticShiftLeft) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (bo instanceof NumericXor) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (bo instanceof Sum) {
			ArithExpr lhsExpr = (ArithExpr) exprFromTerm(bo.getLeft());
			ArithExpr rhsExpr = (ArithExpr) exprFromTerm(bo.getRight());
			return context.mkAdd(lhsExpr, rhsExpr);
		} else if (bo instanceof NumericOr) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (bo instanceof NumericAnd) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (bo instanceof Difference) {
			ArithExpr lhsExpr = (ArithExpr) exprFromTerm(bo.getLeft());
			ArithExpr rhsExpr = (ArithExpr) exprFromTerm(bo.getRight());
			return context.mkSub(lhsExpr, rhsExpr);
		} else {
			throw new UnsupportedOperationException("Case not handled: " + bo);
		}
	}

    /**
     * Begin a new layer of constraints for the incremental constraint solver.
     */
	public void increment() {
        increment(null);
    }

    public void increment(ConstraintExpression ce) {
		this.level++;
		solver.push();
		addedConstraints.push(ce);
	}

    /**
     * Remove a layer of constraints from the incremental constraint solver.
     */
    public void decrement() {
        if (this.level == 0) {
            throw new IllegalStateException("Operation not allowed: Level is already 0.");
        }
        this.level--;
        solver.pop();
        ConstraintExpression ce = addedConstraints.pop();
        if (ce instanceof ArrayStore) {
        	ArrayStore as = (ArrayStore) ce;
        	this.arrayrefsToMostRecentArrayExpr.get(as.getArrayref()).pop();
		}
    }

    public int getLevel() {
        return this.level;
    }

    public Map<Variable, Object> getResults() {
		Model model = solver.getModel();
		Set<Map.Entry<Variable, Expr>> variables = valueConnections.getEntries();
		Map<Variable, Object> variablesToEvaluations = new HashMap<>();
		for (Map.Entry<Variable, Expr> entry : variables) {
			Expr evaluatedExpr = model.evaluate(entry.getValue(), true);
			if (evaluatedExpr.isArray()) {
				throw new UnsupportedOperationException("Not yet supported: " + evaluatedExpr);
			} else if (evaluatedExpr.isString()) {
				throw new UnsupportedOperationException("Not yet supported: " + evaluatedExpr);
			} else if (evaluatedExpr.isIntNum()) {
				variablesToEvaluations.put(entry.getKey(), ((IntNum) evaluatedExpr).getInt());
			} else if (evaluatedExpr.isRatNum()) {
				RatNum ratNum = (RatNum) evaluatedExpr;
				Double val = ((double) ratNum.getNumerator().getInt64()) / (ratNum.getDenominator().getInt64());
				variablesToEvaluations.put(entry.getKey(), val);
			} else if (evaluatedExpr.isBool()) {
				variablesToEvaluations.put(entry.getKey(), ((BoolExpr) evaluatedExpr).isTrue());
			} else {
				throw new UnsupportedOperationException("Not yet supported: " + evaluatedExpr);
			}
		}
		return variablesToEvaluations;
	}

    protected class Z3MugglStore {
		// HashMap maintaining the bijective mapping between variables of the two systems.
		private HashMap<Variable, Expr> mugglToZ3Variable = new HashMap<>();
		private HashMap<Expr, Variable> z3ToMugglVariable = new HashMap<>();

		/**
		 * Obtains the Muggl variable
		 * @param z3Variable Z3 Var object
		 * @return Muggl variable if mapping exists; null otherwise
		 */
		public Object getVariable(Expr z3Variable) {
			return z3ToMugglVariable.get(z3Variable);
		}

		/**
		 * Obtains the Z3 variable
		 * @param mugglVariable Mugl Variable object
		 * @return Z3 variable if mapping exists; null otherwise
		 */
		public Expr getVariable(Variable mugglVariable) {
			return mugglToZ3Variable.get(mugglVariable);
		}

		public Set<Map.Entry<Variable, Expr>> getEntries() {
			return mugglToZ3Variable.entrySet();
		}

		/**
		 * Add a new bijective mapping
		 * @param mugglVariable Variable collected during symbolic exectuion
		 * @param z3Variable New Z3 correspondence
		 */
		public void addVariable(Variable mugglVariable, Expr z3Variable) {
			mugglToZ3Variable.put(mugglVariable, z3Variable);
			z3ToMugglVariable.put(z3Variable, mugglVariable);
		}
	}

}
