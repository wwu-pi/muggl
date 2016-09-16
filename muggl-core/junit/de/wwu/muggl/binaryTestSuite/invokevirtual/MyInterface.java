package de.wwu.muggl.binaryTestSuite.invokevirtual;


public interface MyInterface<E> {
	abstract Object getObj();
	abstract E getObjWithType();

	default String defaultInMyInterface() {

		return getObj().toString();

	}
}
