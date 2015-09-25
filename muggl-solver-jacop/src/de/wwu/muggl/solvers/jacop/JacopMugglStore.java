package de.wwu.muggl.solvers.jacop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.core.Var;
import org.jacop.floats.core.FloatVar;

import de.wwu.muggl.solvers.expressions.Variable;

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
	
	public IntVar[] getIntVariables() {
		Collection<Var> values = mugglToJacopVariable.values();
		ArrayList<IntVar> intVars = new ArrayList<IntVar>(mugglToJacopVariable.size()); 
		for (Var v : values) {
			if (v instanceof IntVar) {
				intVars.add( (IntVar) v );
			}
		}
		
		return intVars.toArray(new IntVar[]{});
	}
	
	public FloatVar[] getFloatVariables() {
		Collection<Var> values = mugglToJacopVariable.values();
		ArrayList<FloatVar> floatVars = new ArrayList<FloatVar>(mugglToJacopVariable.size()); 
		for (Var v : values) {
			if (v instanceof FloatVar) {
				floatVars.add( (FloatVar) v );
			}
		}
		
		return floatVars.toArray(new FloatVar[]{});
	}

}
