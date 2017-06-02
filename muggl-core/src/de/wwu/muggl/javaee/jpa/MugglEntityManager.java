package de.wwu.muggl.javaee.jpa;

import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

/**
 * Special class for the JPA entity manager.
 * 
 * @author Andreas Fuchs
 */
public class MugglEntityManager {

	protected SymbolicDatabase database;
	
	public MugglEntityManager(SymbolicVirtualMachine vm) {
		this.database = new SymbolicDatabase(vm);
	}

	public SymbolicDatabase getDB() {
		return this.database;
	}
	
}
