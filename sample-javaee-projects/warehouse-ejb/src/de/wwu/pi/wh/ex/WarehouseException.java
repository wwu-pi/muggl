package de.wwu.pi.wh.ex;

public class WarehouseException extends Exception {

	private static final long serialVersionUID = 1L;

	public WarehouseException(String s) {
		super(s);
	}
	
	public WarehouseException(String s, Throwable t) {
		super(s, t);
	}
}
