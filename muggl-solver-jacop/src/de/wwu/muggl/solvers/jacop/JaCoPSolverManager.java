package de.wwu.muggl.solvers.jacop;

import de.wwu.muggl.solvers.SolverManagerWithTypeConstraints;
import de.wwu.muggl.solvers.expressions.*;
import org.apache.log4j.Logger;
import org.jacop.core.Domain;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.Var;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatVar;
import org.jacop.floats.search.SmallestDomainFloat;
import org.jacop.floats.search.SplitSelectFloat;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SmallestDomain;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.conf.TesttoolConfig;
import de.wwu.muggl.solvers.conf.SolverManagerConfig;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.solver.listener.SolverManagerListener;
import de.wwu.muggl.solvers.solver.listener.SolverManagerListenerList;

/**
 * JaCoPSolverManager
 * 
 * Integrates the finite domain solver JaCoP into Muggl.
 * 
 * Works as a façade, providing a simple interface to the more complex
 * Muggl-JaCoP-Solver subsystem.
 * Furthermore, it works as an adapter, exposing a Muggl-specific interface
 * for JaCoP-specific internals.
 * 
 * JaCoP is not included; instead, it is a binary dependency of this project.
 * Refer to build.gradle for details.
 * 
 * @author Jan C. Dageförde. 2015
 */
public class JaCoPSolverManager extends SolverManagerWithTypeConstraints implements SolverManager {

	protected boolean finalized = false;

	protected SolverManagerListenerList listeners;

	protected JacopMugglStore jacopStore;

	private Logger logger;

	private long totalConstraintsChecked = 0L;



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
	 * Adds a new constraint the top of the constraint system.
	 * Performs additional transformations in order to obtain JaCoP constraints.
	 * 
	 * @param ce
	 *            the new constraint defined by an expression built by the
	 *            virtual machine.
	 */
	@Override
	public void addConstraintPastChecks(ConstraintExpression ce) {
		jacopStore.setLevel(jacopStore.level + 1);
		_addConstraint(ce);
	}

	protected void _addConstraint(ConstraintExpression ce) {
		if (ce instanceof TypeConstraint) {
			this.imposeTypeConstraint((TypeConstraint) ce);
			//System.out.println("Add: ce: " + ce);
		} else {
			JaCoPTransformer.transformAndImpose(ce, jacopStore);
			// Still call imposeTypeConstraint without an actual constraint to ensure that levels (of type constraints) are consistent with levels (of jacop).
			this.imposeTypeConstraint(null);
		}

		listeners.fireAddConstraint(this, ce, null);

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
	public Solution getSolution()
			throws SolverUnableToDecideException, TimeoutException {

		if (logger.isDebugEnabled())
			logger.debug("getSolution");

		listeners.fireGetSolutionStarted(this);
		long startTime = System.nanoTime();

		if (jacopStore.level == 0) {
			return new Solution();
		}

		IntVar[] vars = jacopStore.getIntVariables();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(vars,
				new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());

		FloatVar[] floatVars = jacopStore.getFloatVariables();
		SplitSelectFloat<FloatVar> selectFloat = new SplitSelectFloat<FloatVar>(
				jacopStore, floatVars, new SmallestDomainFloat<FloatVar>());

		boolean solutionFound;
		Search<?> search;
		
		if (vars.length > 0 && floatVars.length > 0) {
			// Sequential search
			// "The search works in sequence, that is first the parent is solved and **then** the child search is solved.
			// There is backtracking between parent and child search.
			// If, for example, the child search fails it backtracks to the parent search, and so on." (Kris Kuchcinski)

			Search<IntVar> labelInt = new DepthFirstSearch<IntVar>();
			Search<FloatVar> labelFloat = new DepthFirstSearch<FloatVar>();
			labelInt.setPrintInfo(false);
            labelInt.getSolutionListener().recordSolutions(true);
			labelFloat.setPrintInfo(false);
			labelFloat.getSolutionListener().recordSolutions(true);

			labelFloat.setSelectChoicePoint(selectFloat);
			labelInt.addChildSearch(labelFloat);
			
			solutionFound = labelInt.labeling(jacopStore, select) && labelInt.assignSolution();
			search = labelInt;
		} else if (vars.length > 0) {
			// IntVars only
			Search<IntVar> labelInt = new DepthFirstSearch<IntVar>();
            labelInt.getSolutionListener().recordSolutions(true);
            labelInt.setPrintInfo(false);
			solutionFound = labelInt.labeling(jacopStore, select) && labelInt.assignSolution();
			search = labelInt;
		} else if (floatVars.length > 0) {
			// FloatVars only
			Search<FloatVar> labelFloat = new DepthFirstSearch<FloatVar>();
			labelFloat.getSolutionListener().recordSolutions(true);
			labelFloat.setPrintInfo(false);
			solutionFound = labelFloat.labeling(jacopStore, selectFloat) && labelFloat.assignSolution();
			search = labelFloat;
		} else {
            Solution result = new Solution();
            listeners.fireGetSolutionFinished(this, result,
                    System.nanoTime() - startTime);
            return result;
        }
		
		

		Solution result;

		if (!solutionFound) { 
			result = Solution.NOSOLUTION;
		} else {
			result = new Solution();
			Domain[] solution = search.getSolution();
			Var[] variables = search.getVariables();
			for (int i = 0; i < solution.length; i++) {
				Variable variable = jacopStore.getVariable(variables[i]);
				if (variable == null) {
					continue;
				}

				if (solution[i] instanceof IntDomain) {
					result.addBinding(variable, NumericConstant.getInstance(
						((IntDomain) solution[i]).min(), NumericConstant.INT));
				} else {
					result.addBinding(variable, DoubleConstant.getInstance(
						((FloatDomain) solution[i]).min()));
				}
			}

		}
        listeners.fireGetSolutionFinished(this, result,
                System.nanoTime() - startTime);
		return result;

	}

	/**
	 * Checks whether a solution exists for the system of constraints stored in
	 * the current constraint set by using the consistency() method of the
	 * JaCoP store. 
	 * 
	 * @return <i>true</i> if a solution for the given problem exists,
	 *         <i>false</i> if definitively no solution satisfies the
	 *         constraints.
	 * @throws SolverUnableToDecideException
	 *             never; declaration is mandated by the interface
	 * @throws TimeoutException
	 *             never; declaration is mandated by the interface
	 */
	public boolean hasSolution()
			throws SolverUnableToDecideException, TimeoutException {
		if (logger.isDebugEnabled())
			logger.debug("hasSolution: ");

		totalConstraintsChecked++;
		
		listeners.fireHasSolutionStarted(this);
		long startTime = System.nanoTime();

		
		if (jacopStore.level == 0)
			return true;

		// Check consistency with typeConstraints.
        if (this.hasInconsistentTypeConstraints()) {
            return false;
        }

		// "The result true only indicates that inconsistency cannot be found.
		// In other
		// words, since the finite domain solver is not complete it does not
		// automatically mean
		// that the store is consistent."(JaCoP Guide, Ch. 2, p. 12)
		boolean result = jacopStore.consistency();

		listeners.fireHasSolutionFinished(this, result,
				System.nanoTime() - startTime);
		if (logger.isDebugEnabled())
			logger.debug(result);
		return result;

	}

	/**
	 * Removes the lastly added constraint from the constraint stack.
	 * Uses JaCoP's backtracking mechanism to achieve this.
	 */
	public void removeConstraint() {
        int oldLevel = jacopStore.level;
        if (oldLevel <= 0) {
            throw new IllegalStateException(
					"Trying to remove constraint when level is already 0");
        }
		jacopStore.removeLevel(oldLevel);
		jacopStore.setLevel(oldLevel - 1);
		this.removeTypeConstraint();


		listeners.fireConstraintRemoved(this);

		if (logger.isDebugEnabled()) {
			logger.debug("Remove constraint");
		}
		if (logger.isTraceEnabled()) {
			logger.trace(jacopStore.toString());
			logger.trace(jacopStore.toStringChangedEl());
		}

	}

	/**
	 * reset all constraints and statistics and start over with nothing.
	 */
	public void reset() {
		if (logger.isDebugEnabled())
			logger.debug("Reset");

		while (jacopStore.level > 0) {
			jacopStore.removeLevel(jacopStore.level);
			this.removeTypeConstraint();
			jacopStore.setLevel(jacopStore.level - 1);
		}
		// afterwards, jacopStore.level is 0.
		// Assumption: Level is always raised before adding a constraint.
		// Therefore, there are no constraints at level 0 that would need to be
		// removed.
		totalConstraintsChecked = 0;
	}

	private void addShutdownHook() {
		if (Runtime.getRuntime() == null) {
			// Can't do anything. However, this.finalize does not do anything, either.
			return;
		}
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
		return totalConstraintsChecked; //jacopStore.numberConstraints();
	}

	/**
	 * Reset the statistical counter in this class. Added 2008.02.05
	 */
	@Deprecated
	@Override
	public void resetCounter() {
		totalConstraintsChecked = 0;
	}

}