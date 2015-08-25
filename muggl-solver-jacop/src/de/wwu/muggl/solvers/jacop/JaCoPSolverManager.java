package de.wwu.muggl.solvers.jacop;

import org.apache.log4j.Logger;
import org.jacop.core.Domain;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SmallestDomain;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.jacop.listener.SolverManagerListener;
import de.wwu.muggl.solvers.jacop.listener.SolverManagerListenerList;
import de.wwu.testtool.conf.TesttoolConfig;
import de.wwu.testtool.conf.SolverManagerConfig;
import de.wwu.testtool.exceptions.SolverUnableToDecideException;
import de.wwu.testtool.exceptions.TimeoutException;
import de.wwu.testtool.expressions.ConstraintExpression;
import de.wwu.testtool.expressions.NumericConstant;
import de.wwu.testtool.expressions.Variable;

/**
 *
 * @author Jan C. Dageförde. 2015
 */
public class JaCoPSolverManager implements SolverManager {

	protected boolean finalized = false;

	protected SolverManagerListenerList listeners;

	protected JacopMugglStore jacopStore;

	private Logger logger;
	
	/**
	 * Creates a new Solver Manager object and initializes it with a stream that
	 * collects the logging informations if wanted.
	 */
	public JaCoPSolverManager() {
		addShutdownHook();
		logger = Globals.getInst().solverLogger;

		if (logger.isDebugEnabled())
			logger.debug("SolverManager started");

		SolverManagerConfig solverConf = SolverManagerConfig.getInstance();
		
		jacopStore = new JacopMugglStore();

		listeners = new SolverManagerListenerList();
		for (SolverManagerListener listener : solverConf.getListeners()) {
			listeners.addListener(listener);
			if (logger.isDebugEnabled())
				logger.debug("SolverManager: added listener "
					+ listener.getClass().getName());
		}


	}

	/**
	 * Adds a new constraint onto the top of the constraint stack.
	 * 
	 * @param ce
	 *            the new constraint defined by an expression built by the
	 *            virtual machine.
	 * @return the transformed system of constraints that was added to the
	 *         constraint stack.
	 */
	@Override
	public void addConstraint(ConstraintExpression ce) {

		jacopStore.setLevel(jacopStore.level + 1);
		JaCoPTransformer.transformAndImpose(ce, jacopStore);

		listeners.fireAddConstraint(this, ce, null); 

		// TODO use Muggl logging
		if (logger.isDebugEnabled())
				logger.debug("Add: ce: " + ce);
		if (logger.isTraceEnabled()) {
			logger.trace(jacopStore.toString());
			logger.trace(jacopStore.toStringChangedEl());
		}

	}

	@Override
	public void finalize() throws Throwable {
		listeners.fireFinalize(this);
		finalized = true;
		TesttoolConfig.getInstance().finalize();
		super.finalize();
	}

	/**
	 * Tries to find a solution for the first non-contradictory constraint
	 * system contained in the constraint stack of the solver manager.
	 * 
	 * @return a solution for the actual existing constraints or
	 *         Solution.NOSOLUTION if no such solution exists.
	 * @throws SolverUnableToDecideException
	 *             if the used solvers are not able to decide whether a solution
	 *             exists or not or are not able to find an existing solution
	 *             instance.
	 * @throws TimeoutException
	 *             if the used algorithms stop because of reaching the specified
	 *             time limits before being able to decide about the given
	 *             problem.
	 * @see de.wwu.muggl.solvers.Solution#NOSOLUTION
	 */
	@Override
	public Solution getSolution() throws SolverUnableToDecideException,
			TimeoutException {

		if (logger.isDebugEnabled())
			logger.debug("getSolution"); 
		

		listeners.fireGetSolutionStarted(this);
		long startTime = System.nanoTime();
		
		//TODO correct var type? Note: BooleanVar extends IntVar!
		IntVar[] vars = jacopStore.getIntVariables(); /* new IntVar[jacopStore.size()];
		for (int i = 0; i < jacopStore.size(); i++) {
			vars[i] = (IntVar)jacopStore.vars[i];
		}/**///jacopStore.getIntVariables();
		SelectChoicePoint<IntVar> select =
			new SimpleSelect<IntVar>(vars,
			new SmallestDomain<IntVar>(),
			new IndomainMin<IntVar>());

		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		boolean solutionFound = label.labeling(jacopStore, select);
		
		Solution result;
		
		if (!solutionFound || !label.assignSolution()) { // TODO is this assumption on labeling() correct?
			result = Solution.NOSOLUTION;
		} else {
			result = new Solution();
			//label.getSolutionListener().printAllSolutions();
			Domain[] solution = label.getSolution();
            for(int i = 0; i < solution.length; i++) {
            	Variable variable = jacopStore.getVariable(vars[i]);
            	if (variable == null) {
            		continue;
            	}
            	System.out.print(vars[i].id() + " ");
            	System.out.print(variable + " = ");
            	System.out.println(solution[i]);
            	
            	result.addBinding(variable, 
            			NumericConstant.getInstance(((IntDomain)solution[i]).min(), NumericConstant.INT)
            			);
            }
			
		}
		listeners.fireGetSolutionFinished(this, result, System.nanoTime()
				- startTime);
		if (logger.isDebugEnabled())
			System.out.println("solution: " + result);
		
		return result;

		
	}



	/**
	 * Checks whether a solution exists for the system of constraints stored in
	 * the actual constraint stack by using the hasSolution methods of the
	 * constraint solvers. This method may be a little bit faster when only an
	 * assertion about the solvability of a system of constraints should be
	 * calculated.
	 * 
	 * @return <i>true</i> if any solution for the given problem exists,
	 *         <i>false</i> if definitively no solution satisfies the
	 *         constraints.
	 * @throws SolverUnableToDecideException
	 *             if the used solvers are not able to decide whether a solution
	 *             exists or not or are not able to find an existing solution
	 *             instance.
	 * @throws TimeoutException
	 *             if the used algorithms stop because of reaching the specified
	 *             time limits before being able to decide about the given
	 *             problem.
	 */
	public boolean hasSolution() throws SolverUnableToDecideException,
			TimeoutException {

		// RafaC
		if (logger.isDebugEnabled())
			logger.debug("hasSolution: ");

		listeners.fireHasSolutionStarted(this);
		long startTime = System.nanoTime();
		
		// "The result true only indicates that inconsistency cannot be found. In other
		// words, since the finite domain solver is not complete it does not automatically mean
		// that the store is consistent."(JaCoP Guide, Ch. 2, p. 12)
		boolean result = jacopStore.consistency();
		
		listeners.fireHasSolutionFinished(this, result , System.nanoTime()
				- startTime);
														if (logger.isDebugEnabled())
															logger.debug(result);
		return result;

		/*if (constraintStack.getSystemCount() == 0) {
			listeners.fireHasSolutionFinished(this, true, System.nanoTime()
					- startTime);
														if (logger.isDebugEnabled())
															logger.debug("true ");

			return true;
		}

		int idx = constraintStack.getFirstNoncontradictorySystemIndex();
		while (idx < constraintStack.getSystemCount()) {
			if (hasSolution(idx)) {
				listeners.fireHasSolutionFinished(this, true, System.nanoTime()
						- startTime);
														if (logger.isDebugEnabled())
															logger.debug("true ");
				return true;
			}

			idx++;
		}
		listeners.fireHasSolutionFinished(this, false, System.nanoTime()
				- startTime);
														if (logger.isDebugEnabled())
															logger.debug("false");
		return false;*/
	}

	/**
	 * Removes the lastly added constraint from the constraint stack.
	 */
	public void removeConstraint() {
		if (jacopStore.level <= 0) {
			throw new IllegalStateException("Trying to remove constraint when level is already 0");
		}
		jacopStore.removeLevel(jacopStore.level);
		jacopStore.setLevel(jacopStore.level - 1);
		
		listeners.fireConstraintRemoved(this);

		if (logger.isDebugEnabled()) {
			logger.debug("Remove constraint");
		}
		if (logger.isTraceEnabled()) {	
			logger.trace(jacopStore.toString());
			logger.trace(jacopStore.toStringChangedEl());
		}
															
	}

	public void reset() {
		if (logger.isDebugEnabled())
			logger.debug("Reset");

		while (jacopStore.level > 0) {
			jacopStore.removeLevel(jacopStore.level);
			jacopStore.setLevel(jacopStore.level - 1);
		}
		// afterwards, jacopStore.level is 0. 
		// Assumption: Level is always raised before adding a constraint.
		// Therefore, there are no constraints at level 0 that would need to be removed.
	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					this.finalize();
				} catch (Throwable t) {
					// do nothing
				}
			}
		});
	}

	/**
	 * Getter for the total number of constraints checked with hasSolution().
	 * Added 2008.02.05
	 * 
	 * @return The total number of constraints checked.
	 */
	@Deprecated
	@Override
	public long getTotalConstraintsChecked() {
		return jacopStore.numberConstraints();
	}

	/**
	 * Reset the statistical counter in this class. Added 2008.02.05
	 */
	@Deprecated
	@Override
	public void resetCounter() {
	}

}