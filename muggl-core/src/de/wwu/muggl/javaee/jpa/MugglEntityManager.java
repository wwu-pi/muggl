package de.wwu.muggl.javaee.jpa;

import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

/**
 * Special class for the JPA entity manager.
 * 
 * @author Andreas Fuchs
 */
public class MugglEntityManager {

	// the symoblic database
	protected SymbolicDatabase database;
	
	// helper class to analyze entity class files
	protected JPAEntityClassAnalyzer entityAnalyzer;
	
	public MugglEntityManager(SymbolicVirtualMachine vm) {
		this.database = new SymbolicDatabase(vm);
	}

	public SymbolicDatabase getDB() {
		return this.database;
	}
	
	public JPAEntityClassAnalyzer getEntityAnalyzer() {
		return this.entityAnalyzer;
	}
}
