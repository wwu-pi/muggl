package de.wwu.muggl.solvers.jacop;

import java.util.HashMap;

import org.jacop.core.Store;
import org.jacop.core.Var;

import de.wwu.testtool.expressions.Variable;

public class JacopMugglStore extends Store {
	private HashMap<Variable, Var> mugglToJacopVariable;
	private HashMap<Var, Variable> jacopToMugglVariable;

	public JacopMugglStore() {
		jacopToMugglVariable = new HashMap<Var, Variable>();
		mugglToJacopVariable = new HashMap<Variable, Var>();
	}

	public Variable getVariable(Var jacopVariable) {
		return jacopToMugglVariable.get(jacopVariable);
	}
	
	public Var getVariable(Variable mugglVariable) {
		return mugglToJacopVariable.get(mugglVariable); 
	}

	public void addVariable(Variable mugglVariable, Var jacopVariable) {
		mugglToJacopVariable.put(mugglVariable, jacopVariable);
		jacopToMugglVariable.put(jacopVariable, mugglVariable);
	}

}
