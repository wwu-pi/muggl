package de.wwu.muggl.javaee.solution;

import java.util.HashSet;
import java.util.Set;

public class SolutionManager {

	protected static int solutionCounter = 0;
	
	protected static Set<SolutionWrapper> solutions = new HashSet<>();
	
	public static void addSolution(String constraints, String returnValue, String solution) {
		solutions.add(new SolutionWrapper(constraints,returnValue,solution));
	}
	
	public static Set<SolutionWrapper> getSolutions() {
		return solutions;
	}
}
