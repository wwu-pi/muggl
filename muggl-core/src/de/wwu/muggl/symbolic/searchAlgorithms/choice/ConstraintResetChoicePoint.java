package de.wwu.muggl.symbolic.searchAlgorithms.choice;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public abstract class ConstraintResetChoicePoint implements ChoicePoint {

	protected int constraintLevel;
	
	public ConstraintResetChoicePoint(Frame frame) throws SymbolicExecutionException {
		VirtualMachine vm = frame.getVm();
		if(vm instanceof SymbolicVirtualMachine ) {
			this.constraintLevel = ((SymbolicVirtualMachine)vm).getSolverManager().getConstraintLevel();
		} else {
			throw new SymbolicExecutionException("Cannot create a choice point in a non-symbolic mode!");
		}
	}
	
	public int getConstraintLevel() {
		return this.constraintLevel;
	}
}
