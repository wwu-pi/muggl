/**
 * 
 */
package de.wwu.testtool.solver.jacop;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jacop.constraints.Constraint;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.*;

import de.wwu.testtool.solver.Solution;
import de.wwu.testtool.expressions.*;
import de.wwu.testtool.solver.constraints.*;

/**
 * A JaCoP store adapted to the use for Muggle.
 * Contains mainly a Store and the list of variables
 * @author rafa
 *  @version 1.0. 22 July 2014.
 */
public class JaCoPStore {
	/**
	 * The JaCoP store including all the constraints added up to now.
	 */
	protected Store store=null;
	/**
	* The JaCoP variables that have occurred in the constraints stored in {@link Store}
	*/
	protected ArrayList<IntVar> vars=null;
	
	/**
	 *  Debugging information
	 */
	private Logger logger=null;

	
	public JaCoPStore(Logger logger) {
		store = new Store();
		vars = new ArrayList<IntVar>();
		this.logger = logger;
	}

	/** 
	 * Adds a new {@link ComposedConstraint} to the store
	 * This affects both to the attributes {@link Store}
	 * and {@vars}. The big work here is to convert the constraint
	 * from Muggl to JaCoP, done by {@link convert}.
	 * @param ce The new constraint in Muggle format.
	 */
	public void add(ComposedConstraint cc) {
														if (logger.isTraceEnabled())
															logger.trace("Adding constraint of class "+cc.getClass().toString());		
	

		Converter v = new Converter(store,vars,logger);
		Constraint c = v.convert(cc);
		if (c!=null)
		    store.impose(c);
	}

	public boolean hasSolution() {
		boolean r;
		r = store.consistency();
		return r;
	}

	/**
	 * Search for a solution and convert result into a Muggle Solution
	 * 
	 * @return The Muggle solution. Solution.NOSOLUTION is there is not solution
	 */
	public Solution getSolution() {
		Solution r = null;
		if (vars.size() == 0)
			r = Solution.NOSOLUTION;
		else {
			// convert the list of variables into a proper array.
			// this is required by JaCoP
			IntVar[] v = new IntVar[vars.size()];
			for (int i = 0; i < vars.size(); i++)
				v[i] = vars.get(i);

			// search for a solution and convert result into a Muggle Solution
			Search<IntVar> search = new DepthFirstSearch<IntVar>();
			SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(
					store, v, new IndomainMin<IntVar>());
			// labeling returns false if there is no solution
			boolean result = search.labeling(store, select);
			if (!result)
				r = Solution.NOSOLUTION;
			else {
				// convert the solution into Muggle Format
				r = new Solution();
				for (int i = 0; i < v.length; i++) {
					NumericVariable var = new NumericVariable(v[i].id(),
							Expression.INT);
					int val = Integer.parseInt("" + v[i]);
					NumericConstant value = NumericConstant.getInstance(val,
							Expression.INT);
					r.addBinding(var, value);
				}
			}
		}
		return r;
	}

	
}
