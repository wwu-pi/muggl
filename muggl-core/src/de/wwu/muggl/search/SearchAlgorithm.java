package de.wwu.muggl.search;


/**
 * This interface is to be extended by other interfaces, specifying methods to be
 * used by different kinds of virtual machines. 
 * At the time of conception, two kinds are considered:
 * * Symbolic JVMs
 * * Logic JVMs
 * @author Jan Dageförde
 * @version 1.0.0
 */
public interface SearchAlgorithm {

	/**
	 * Return a String representation of this search algorithms name.
	 *
	 * @return A String representation of this search algorithms name.
	 */
	String getName();

}