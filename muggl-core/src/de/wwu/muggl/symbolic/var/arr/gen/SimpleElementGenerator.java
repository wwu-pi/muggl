package de.wwu.muggl.symbolic.var.arr.gen;

import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericEqual;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;

/**
 * A simple element generator that just returns a object reference variable for the specified type.
 * 
 * @author Andreas Fuchs
 *
 */
public class SimpleElementGenerator implements ArrayElementGenerator {

	protected int count;
	
	protected String parentName;
	
	protected InitializedClass type;
	
	protected SymbolicVirtualMachine vm;
	
	
	public SimpleElementGenerator(String parentName, InitializedClass type, SymbolicVirtualMachine vm) {
		this.count = 0;
		this.parentName = parentName;
		this.vm = vm;
		this.type = type;
	}
	
	@Override
	public ObjectrefVariable generateElement() {
		ObjectrefVariable ele = new ObjectrefVariable(parentName+".genElement_"+count++, type, vm);
		vm.getSolverManager().addConstraint(NumericEqual.newInstance(ele.getIsNullVariable(), NumericConstant.getZero(Expression.BOOLEAN)));
		return ele;
	}

}
