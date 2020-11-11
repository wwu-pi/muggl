package de.wwu.muggl.solvers.z3;

import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.ParamDescrs;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.SolverManagerWithTypeConstraints;
import de.wwu.muggl.solvers.conf.SolverManagerConfig;
import de.wwu.muggl.solvers.conf.TesttoolConfig;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.solvers.solver.listener.SolverManagerListener;
import de.wwu.muggl.solvers.solver.listener.SolverManagerListenerList;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Z3SolverManager
 * 
 * Integrates the SMT solver Z3 into Muggl/Muli.
 * 
 * Works as a façade, providing a simple interface to the more complex
 * Muggl-Z3-Solver subsystem.
 * Furthermore, it works as an adapter, exposing a Muggl-specific interface
 * for Z3-specific internals.
 * 
 * Z3 is not included; instead, it is a binary dependency of this project.
 * Refer to build.gradle for details.
 * 
 * @author Jan C. Dageförde, Hendrik Winkelmann 2020
 */
public class Z3SolverManager extends SolverManagerWithTypeConstraints implements SolverManager {
	protected boolean finalized = false;
	protected SolverManagerListenerList listeners;
	protected Z3MugglAdapter z3;
	private Logger logger;
	private long totalConstraintsChecked = 0L;
	private boolean isSatisfiable = true;
	private boolean satisfiabilityWasCalculated = true;

	/**
	 * Creates a new Solver Manager object and initializes it with a stream that
	 * collects the logging informations if wanted.
	 */
	public Z3SolverManager() {
		addShutdownHook();
		logger = Globals.getInst().solverLogger;

		if (logger.isDebugEnabled())
			logger.debug("Z3SolverManager started");

		SolverManagerConfig solverConf = SolverManagerConfig.getInstance();

		z3 = new Z3MugglAdapter();

		listeners = new SolverManagerListenerList();
		for (SolverManagerListener listener : solverConf.getListeners()) {
			listeners.addListener(listener);
			if (logger.isDebugEnabled())
				logger.debug("SolverManager: added listener "
						+ listener.getClass().getName());
		}

	}

	protected boolean isSatisfiable() throws TimeoutException, SolverUnableToDecideException {
		return satisfiabilityWasCalculated ? isSatisfiable : hasSolution();
	}

	/**
	 * Adds a new constraint the top of the constraint system.
	 * Performs additional transformations in order to obtain Z3 constraints.
	 * 
	 * @param ce
	 *            the new constraint defined by an expression built by the
	 *            virtual machine.
	 */
	@Override
	public void addConstraint(ConstraintExpression ce) {
		z3.increment();
		satisfiabilityWasCalculated = false;
		if (ce instanceof TypeConstraint) {
			imposeTypeConstraint((TypeConstraint) ce);
		} else {
			z3.imposeConstraint(ce);
			// Still call imposeTypeConstraint without an actual constraint to ensure that levels (of type constraints) are consistent with levels (of Z3).
			imposeTypeConstraint(null);
		}
		listeners.fireAddConstraint(this, ce, null);

		if (logger.isDebugEnabled())
			logger.debug("Add: ce: " + ce);
		if (logger.isTraceEnabled()) {
			logger.trace(z3.toString());
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

		if (z3.getLevel() == 0) {
			return new Solution();
		}

		Solution result;

		Map<Variable, Object> results = z3.getResults();
        boolean solutionFound = isSatisfiable();

		if (!solutionFound) {
			result = Solution.NOSOLUTION;
		} else {
			result = new Solution();
			for (Map.Entry<Variable, Object> entry : results.entrySet()) {
				Variable v = entry.getKey();
				if (v instanceof NumericVariable) {
					result.addBinding(v, NumericConstant.getInstance((entry.getValue())));
				} else if (v instanceof BooleanVariable) {
					result.addBinding(v, BooleanConstant.getInstance((Boolean) entry.getValue()));
				} else {
					throw new UnsupportedOperationException("Not yet implemented");
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
	 * Z3 store.
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

		
		if (z3.getLevel() == 0)
			return true;

		// Check consistency with typeConstraints.
        if (this.hasInconsistentTypeConstraints()) {
            return false;
        }

        boolean result = z3.isSatisfiable();
        isSatisfiable = result;
		satisfiabilityWasCalculated = true;

		listeners.fireHasSolutionFinished(this, result,
				System.nanoTime() - startTime);
		if (logger.isDebugEnabled())
			logger.debug(result);
		return result;
	}

	/**
	 * Removes the lastly added constraint from the constraint stack.
	 * Uses Z3's backtracking mechanism to achieve this.
	 */
	public void removeConstraint() {
        int oldLevel = z3.getLevel();
        if (oldLevel <= 0) {
            throw new IllegalStateException(
					"Trying to remove constraint when level is already 0");
        }
		z3.decrement();
		this.removeTypeConstraint();

		listeners.fireConstraintRemoved(this);

		if (logger.isDebugEnabled()) {
			logger.debug("Remove constraint");
		}
		if (logger.isTraceEnabled()) {
			logger.trace(z3.toString());
		}

	}

	/**
	 * reset all constraints and statistics and start over with nothing.
	 */
	public void reset() {
		if (logger.isDebugEnabled())
			logger.debug("Reset");

		while (z3.getLevel() > 0) {
			z3.decrement();
            this.removeTypeConstraint();
		}
		// afterwards, Z3Store.level is 0.
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
		return totalConstraintsChecked; //Z3Store.numberConstraints();
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