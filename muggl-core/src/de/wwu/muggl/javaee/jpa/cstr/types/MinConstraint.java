package de.wwu.muggl.javaee.jpa.cstr.types;

import de.wwu.muggl.vm.classfile.structures.Field;

public class MinConstraint extends JPAStaticConstraint {

	protected int minValue;
	
	public MinConstraint(String entityClassName, Field field, int minValue) {
		super(entityClassName, field);
		this.minValue = minValue;
	}

	public int getMinValue() {
		return this.minValue;
	}
}
