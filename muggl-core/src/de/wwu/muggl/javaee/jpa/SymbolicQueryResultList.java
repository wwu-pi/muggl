package de.wwu.muggl.javaee.jpa;

import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericEqual;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;

public class SymbolicQueryResultList extends ObjectrefVariable {

	protected MugglTypedQuery query;
	
	public SymbolicQueryResultList(String name, MugglTypedQuery query, SymbolicVirtualMachine vm) {
		super(name, getListReference(vm), vm);
		
		this.query = query;
		
		// set this list to be NOT null
		vm.getSolverManager().addConstraint(NumericEqual.newInstance(this.isNull, NumericConstant.getZero(Expression.BOOLEAN)));
	}

	private static InitializedClass getListReference(SymbolicVirtualMachine vm) {
		try {
			ClassFile listClassFile = vm.getClassLoader().getClassAsClassFile(java.util.ArrayList.class.getName());
			return vm.getAnObjectref(listClassFile).getInitializedClass();
		} catch (ClassFileException e) {
			e.printStackTrace();
		}
		return null;
	}

}
