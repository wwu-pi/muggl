package de.wwu.muggl.solvers.z3;

import com.microsoft.z3.*;
import de.wwu.muggl.solvers.expressions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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

	public Z3MugglAdapter() {
		context = new Context();
		Params parameters = context.mkParams();
		parameters.add("timeout", 20000);
		parameters.add("auto_config", true);
		parameters.add("model", true);
		//context.usingParams(Tactic)
		solver = context.mkSolver();
	}

	private void newBacktrackPoint() {
		solver.push();
	}

	private void backtrackOnce() {
		solver.pop();
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
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
		} else if (ce instanceof Or) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
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
		} else {
			throw new UnsupportedOperationException("Case not handled: " + ce);
		}
	}

	private BoolExpr getSingleConstraintExpression(SingleConstraintExpression sce) {
		if (sce instanceof BooleanConstant) {
			throw new UnsupportedOperationException("Case not yet regarded."); // TODO
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
			result = context.mkIntConst(nv.getInternalName());
		} else {
			result = context.mkRealConst(nv.getInternalName());
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
        this.level++;
        newBacktrackPoint();
    }

    /**
     * Remove a layer of constraints from the incremental constraint solver.
     */
    public void decrement() {
        if (this.level == 0) {
            throw new IllegalStateException("Operation not allowed: Level is already 0.");
        }
        this.level--;
        backtrackOnce();
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
		public Variable getVariable(Expr z3Variable) {
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
