package de.wwu.muggl.javaee.solution;

public class SolutionWrapper {

	protected String constraints;
	protected String returnValue;
	protected String solution;
	
	public SolutionWrapper(String constraints, String returnValue, String solution) {
		super();
		this.constraints = constraints;
		this.returnValue = returnValue;
		this.solution = solution;
	}
	
	public String getConstraints() {
		return constraints;
	}
	
	public String getReturnValue() {
		return returnValue;
	}
	
	public String getSolution() {
		return solution;
	}
	
	@Override
	public String toString() {
		return "Constraints:\n"+constraints
				+"\n--------\n"
				+"Solution:\n"+solution
				+"\n--------\n"
				+"Return Value:\n"+returnValue
				+"\n--------\n";
	}
	
}
