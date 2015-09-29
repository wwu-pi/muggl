package de.wwu.muggl.solvers.jacop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.core.Var;
import org.jacop.floats.core.FloatVar;

import de.wwu.muggl.solvers.expressions.Variable;

/**
 * JaCoPMugglStore
 * 
 * Specialises the JaCoP store to maintain some Muggl-specifc state of the store:
 * Correspondence between variables of the two systems 
 * 
 * @author Jan C. Dageförde, 2015.
 *
 */
public class JacopMugglStore extends Store {
	/** 
	 * HashMap maintaining the bijective mapping between variables of the two systems.
	 */
	private HashMap<Variable, Var> mugglToJacopVariable;
	/** 
	 * HashMap maintaining the bijective mapping between variables of the two systems.
	 */
	private HashMap<Var, Variable> jacopToMugglVariable;

	public JacopMugglStore() {
		jacopToMugglVariable = new HashMap<Var, Variable>();
		mugglToJacopVariable = new HashMap<Variable, Var>();
	}

	/**
	 * Obtains the Muggl variable
	 * @param jacopVariable JaCoP Var object
	 * @return Muggl variable if mapping exists; null otherwise
	 */
	public Variable getVariable(Var jacopVariable) {
		return jacopToMugglVariable.get(jacopVariable);
	}

	/**
	 * Obtains the JaCoP variable
	 * @param mugglVariable Mugl Variable object
	 * @return JaCoP variable if mapping exists; null otherwise
	 */	
	public Var getVariable(Variable mugglVariable) {
		return mugglToJacopVariable.get(mugglVariable); 
	}

	/**
	 * Add a new bijective mapping
	 * @param mugglVariable Variable collected during symbolic exectuion
	 * @param jacopVariable New JaCoP correspondence
	 */
	public void addVariable(Variable mugglVariable, Var jacopVariable) {
		mugglToJacopVariable.put(mugglVariable, jacopVariable);
		jacopToMugglVariable.put(jacopVariable, mugglVariable);
	}
	
	/**
	 * Retrieve all IntVars that correspond to a Muggl variable
	 * @return Array of variables
	 */
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
	

	/**
	 * Retrieve all FloatVars that correspond to a Muggl variable
	 * @return Array of variables
	 */
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
