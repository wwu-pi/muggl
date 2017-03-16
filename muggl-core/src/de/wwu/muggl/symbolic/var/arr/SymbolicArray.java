package de.wwu.muggl.symbolic.var.arr;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.solvers.expressions.NumericVariable;

/**
 * @author Andreas Fuchs
 */
public class SymbolicArray {

	/**
	 * Elements of this array in 'concrete' position, i.e. integer key is index in array.
	 */
	protected Map<Integer, Object> elementMap;
	
	/**
	 * Elements of this array in 'symbolic' position, i.e. numeric variable is index in array.
	 * This map must be compliant with {@link #elementMap}.
	 */
	protected Map<NumericVariable, Object> symElementMap;
	
	public SymbolicArray() {
		this.elementMap = new HashMap<>();
		this.symElementMap = new HashMap<>();
	}
	
	public void addElementAt(int index, Object element) {
		this.elementMap.put(index, element);
	}
	
	public void addElementAt(NumericVariable index, Object element) {
		this.symElementMap.put(index, element);
	}
	
}
