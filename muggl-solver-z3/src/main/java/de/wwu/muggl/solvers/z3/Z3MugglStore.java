package de.wwu.muggl.solvers.z3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import de.wwu.muggl.solvers.expressions.Variable;

/**
 * Z3MugglStore
 * 
 * Specialises the Z3 store to maintain some Muggl-specifc state of the store:
 * Correspondence between variables of the two systems 
 * 
 * @author Jan C. Dageförde, 2020.
 *
 */
public class Z3MugglStore { // TODO extends Store (or maybe not).
	/** 
	 * HashMap maintaining the bijective mapping between variables of the two systems.
	 */
	private HashMap<Variable, Integer> mugglToZ3Variable;
	/** 
	 * HashMap maintaining the bijective mapping between variables of the two systems.
	 */
	private HashMap<Integer, Variable> z3ToMugglVariable;

	private int level = 0;

	public Z3MugglStore() {
		z3ToMugglVariable = new HashMap<Integer, Variable>();
		mugglToZ3Variable = new HashMap<Variable, Integer>();

		Context ctx = new Context();
        Solver solver = ctx.mkSolver();
        solver.pop();
    }

	/**
	 * Obtains the Muggl variable
	 * @param z3Variable Z3 Var object
	 * @return Muggl variable if mapping exists; null otherwise
	 */
	public Variable getVariable(Integer z3Variable) {
		return z3ToMugglVariable.get(z3Variable);
	}

	/**
	 * Obtains the Z3 variable
	 * @param mugglVariable Mugl Variable object
	 * @return Z3 variable if mapping exists; null otherwise
	 */	
	public Integer getVariable(Variable mugglVariable) {
		return mugglToZ3Variable.get(mugglVariable);
	}

	/**
	 * Add a new bijective mapping
	 * @param mugglVariable Variable collected during symbolic exectuion
	 * @param z3Variable New Z3 correspondence
	 */
	public void addVariable(Variable mugglVariable, Integer z3Variable) {
		mugglToZ3Variable.put(mugglVariable, z3Variable);
		z3ToMugglVariable.put(z3Variable, mugglVariable);
	}

    /**
     * Begin a new layer of constraints for the incremental constraint solver.
     */
	public void increment() {
        this.level++;
        // TODO (perhaps there is actually nothing to do).
    }

    /**
     * Remove a layer of constraints from the incremental constraint solver.
     */
    public void decrement() {
        if (this.level == 0) {
            throw new IllegalStateException("Operation not allowed: Level is already 0.");
        }
        this.level--;
        // TODO Remove constraints from that level.
    }

    public int getLevel() {
        return this.level;
    }

}
