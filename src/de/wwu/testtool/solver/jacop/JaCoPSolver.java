/**
 * 
 */
package de.wwu.testtool.solver.jacop;

import java.util.Stack;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.wwu.muggl.configuration.Globals;
import de.wwu.testtool.conf.TesttoolConfig;
import de.wwu.testtool.expressions.ConstraintExpression;
import de.wwu.testtool.solver.Solution;
import de.wwu.testtool.solver.constraints.ComposedConstraint;

/**
 * 
 * Solver based on JaCoP finite domain solver.
 * It simulates the features needed by muggle, namely incremental adding 
 * and removing constraints.
 * In the current implementation this is done using an Stack of {@link JaCoPStore}
 * elements.
 * @author rafa
 * @version 1.0. 22 July 2014.
 */
public class JaCoPSolver {
	
	/**
	 * The constraints are stored for simulating incremental solving
	 */
	protected Stack<ComposedConstraint> stackcts=null;
	/**
	 * The JaCoP Stores are stored for simulating incremental solving
	 */
	protected Stack<JaCoPStore> stackjacop=null;
	
	/**
	 *  Debugging information
	 */
	private Logger logger;

	/**
	 * Constructs a new solver. The stacks are initialized to empty.
	 */
	public JaCoPSolver() {
		logger = Globals.getInst().jacopLogger;
		if (logger.isDebugEnabled())
				logger.debug("JaCoP. started");
        reset(); 		
	}
	
	
	/**
	 * Initializes the attributes to empty values.
	 */
	public void reset() {
													if (logger.isDebugEnabled())
														logger.debug("reset");

		stackcts = new Stack<ComposedConstraint>();
		stackjacop = new Stack<JaCoPStore>(); 		
	}

	/**
	 * Adds a new constraint to the store
	 * @param ce Constraint to be added to the store
	 */
	public void add(ComposedConstraint cc) {
													if (logger.isDebugEnabled())
														logger.debug("add "+cc.toString());
		
		if (stackcts != null) {
			stackcts.push(cc);			
													if (logger.isTraceEnabled()) 
														logger.trace("The stack of constraints has "+stackcts.size()+" elements: "+stackcts.toString());
								

		} 
													if (stackcts == null && logger.isEnabledFor(Level.FATAL))
														logger.fatal("Unexpected error: stackcts is null");			
		if (stackjacop != null) {
			JaCoPStore store = new JaCoPStore(logger);
			for (int i=0; i<stackcts.size(); i++) {
				store.add(stackcts.get(i));
			}
			stackjacop.push(store);	
		}
													if (stackjacop == null && logger.isEnabledFor(Level.FATAL))
														logger.fatal("Unexpected error: stackjacop is null");			
			
	}

	/**
	 * @return The solution of the current store
	 */
	public Solution getSolution() {
		Solution r = Solution.NOSOLUTION;
													if (logger.isDebugEnabled())
															logger.debug("Solution " + r);

		if (stackjacop != null)
			if (!stackjacop.isEmpty()) {
				JaCoPStore store = stackjacop.peek();
				r = store.getSolution();
			} else if (logger.isEnabledFor(Level.FATAL))
				logger.fatal("Unexpected error in getSolution: stackjacop is empty");

			else if (logger.isEnabledFor(Level.FATAL))
				logger.fatal("Unexpected error in getSolution: stackjacop is null");

		return r;
	}

	/**
	 * @return false means that the current system has no solution.
	 *         true only means that an inconsistency has not been detected
	 */
	public boolean hasSolution() {
		boolean r=false;
													if (logger.isDebugEnabled())
														logger.debug("hasSolution "+r);

		if (stackjacop!=null)
			if (!stackjacop.isEmpty()) {
				JaCoPStore store = stackjacop.peek();
				r = store.hasSolution();
			}
			else
				if (logger.isEnabledFor(Level.FATAL))
					logger.fatal("Unexpected error in hasSolution: stackjacop is empty");			
		
		else
			if (logger.isEnabledFor(Level.FATAL))
				logger.fatal("Unexpected error in hasSolution: stackjacop is null");			

		return r;
	}

	/**
	 * removes the last constraint from the store
	 */
	public void remove() {
													if (logger.isDebugEnabled())
														logger.debug("remove ");
       stackcts.pop();
       												if (logger.isTraceEnabled()) 
       													logger.trace("The stack of constraints has "+stackcts.size()+" elements: "+stackcts.toString());
	}

}
