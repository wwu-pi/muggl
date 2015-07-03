package de.wwu.logic.solutions;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Iterable class that stores solutions generated by logic computation.
 * 
 * @param <T> The type of the solutions.
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-07-20
 */
public final class Solutions<T> implements Iterable<Solution<T>> {
	private Set<Solution<T>> solutions;
	
	/**
	 * Default constructor.
	 */
	public Solutions() {
		this.solutions = new TreeSet<Solution<T>>();
	}
	
	/**
	 * Constructor used for an empty solution.
	 * 
	 * @param solution An EmptySolution object.
	 */
	@SuppressWarnings("unused")
	public Solutions(EmptySolution solution) {
		this();
	}
	
	/**
	 * Constructor that stores the first solution.
	 * 
	 * @param solution A solution.
	 */
	public Solutions(T solution) {
		this();
		this.solutions.add(new Solution<T>(solution));
	}

	/**
	 * Constructor that stores the first solution.
	 * 
	 * @param solution A solution.
	 */
	public Solutions(Solution<T> solution) {
		this();
		this.solutions.add(solution);
	}
	
	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Solution<T>> iterator() {
		// TODO
		return null;
	}
	
	/**
	 * Get all solutions.
	 *
	 * @return The solutions.
	 */
	Set<Solution<T>> getSolutions() {
		return this.solutions;
	}

}