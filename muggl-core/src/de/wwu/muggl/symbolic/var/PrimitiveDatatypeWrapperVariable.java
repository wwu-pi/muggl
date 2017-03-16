package de.wwu.muggl.symbolic.var;

import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;

/**
 * This is an object reference variable for primitive data types, 
 * i.e Integer, Double, Float, Boolean, etc.
 * 
 * @author Andreas Fuchs
 *
 */
public class PrimitiveDatatypeWrapperVariable extends ObjectrefVariable {

	/**
	 * The primitive data type.
	 */
	protected byte type;
	
	/**
	 * The internal variable for this primitive data type wrapper.
	 */
	protected NumericVariable internalVariable;
	

	/**
	 * @param type the primitive data type
	 */
	public PrimitiveDatatypeWrapperVariable(String name, InitializedClass staticReference, byte type, SymbolicVirtualMachine vm) {
		super(name, staticReference, true, vm);
		this.type = type;
		this.internalVariable = new NumericVariable(name, type);
	}

	@Override
	public String toString() {
		
		return this.name +" ("+this.getClass().getSimpleName()+", " + this.getExpressionString()+")";
	}
	
	private String getExpressionString() {
		switch(type) {
			case BYTE : return "BYTE";
			case SHORT : return "SHORT";
			case INT : return "INT";
			case LONG : return "LONG";
			case BOOLEAN : return "BOOLEAN";
			case DOUBLE : return "DOUBLE";
			case FLOAT : return "FLOAT";
			case CHAR : return "CHAR";
			default: return "<not defined>";
		}
	}

	public NumericVariable getInternalVariable() {
		return this.internalVariable;
	}
}
