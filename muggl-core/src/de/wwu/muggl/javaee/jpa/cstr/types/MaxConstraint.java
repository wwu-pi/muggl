package de.wwu.muggl.javaee.jpa.cstr.types;

import de.wwu.muggl.vm.classfile.structures.Field;

public class MaxConstraint extends JPAStaticConstraint {

	protected int maxValue;
	
	public MaxConstraint(String entityClassName, Field field, int maxValue) {
		super(entityClassName, field);
		this.maxValue = maxValue;
	}
	
	public int getMaxValue() {
		return this.maxValue;
	}

}
