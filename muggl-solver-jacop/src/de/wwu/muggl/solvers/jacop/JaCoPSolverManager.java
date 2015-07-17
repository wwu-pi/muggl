package de.wwu.muggl.solvers.jacop;

import org.apache.log4j.Logger;
import org.jacop.constraints.Constraint;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SmallestDomain;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.jacop.listener.SolverManagerListener;
import de.wwu.muggl.solvers.jacop.listener.SolverManagerListenerList;
import de.wwu.testtool.conf.TesttoolConfig;
import de.wwu.testtool.conf.SolverManagerConfig;
import de.wwu.testtool.exceptions.IncorrectSolverException;
import de.wwu.testtool.exceptions.SolverUnableToDecideException;
import de.wwu.testtool.exceptions.TimeoutException;
import de.wwu.testtool.expressions.ConstraintExpression;
import de.wwu.testtool.solver.HasSolutionInformation;
import de.wwu.testtool.solver.constraints.Assignment;
import de.wwu.testtool.solver.constraints.ComposedConstraint;
import de.wwu.testtool.solver.constraints.SingleConstraintSet;
import de.wwu.testtool.solver.constraints.ConstraintStack;
import de.wwu.testtool.solver.constraints.ConstraintSystem;
import de.wwu.testtool.solver.constraints.SingleConstraint;
import de.wwu.testtool.solver.tools.SubstitutionTable;
import de.wwu.testtool.solver.tsolver.Solver;

/**
 *
 * @author Jan C. Dageförde
 */
public class JaCoPSolverManager implements SolverManager {

	private static long totalConstraintsChecked = 0;

	protected boolean finalized = false;

	protected SolverManagerListenerList listeners;

	protected Store jacopStore;

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
		
		jacopStore = new Store();

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
	public Constraint addConstraint(ConstraintExpression ce) {

		Constraint jacopConstraint = transformToJaCoPConstraint(ce);
		
		jacopStore.impose(jacopConstraint);

		listeners.fireAddConstraint(this, ce, null); // `null` WAS `cc` -- how to deal with composed constraints in jacop?

		// TODO use Muggl logging
		if (logger.isDebugEnabled())
				logger.debug("Add: ce: " + ce + ". jacop: " + jacopConstraint.toString());
		if (logger.isTraceEnabled()) {
			logger.trace(jacopStore.toString());
			logger.trace(jacopStore.toStringChangedEl());
		}

		return jacopConstraint;
	}

	private Constraint transformToJaCoPConstraint(ConstraintExpression ce) {
		throw new NotImplementedException();
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
		IntVar[] vars = null; // TODO set vars from store -- maybe beforehand
		SelectChoicePoint<IntVar> select =
			new SimpleSelect<IntVar>(vars,
			new SmallestDomain<IntVar>(),
			new IndomainMin<IntVar>());

		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		boolean labellingSuccessful = label.labeling(jacopStore, select);
		
		Solution result = null;
		
		if (!labellingSuccessful) { // TODO is this assumption on labeling() correct?
			result = Solution.NOSOLUTION;
		} else {
			//TODO fill result
		}
		listeners.fireGetSolutionFinished(this, result, System.nanoTime()
				- startTime);
		if (logger.isDebugEnabled())
			System.out.println("solution: " + result);
		
		return result;

		
	}

	/**
	 * Tries to find a solution for the system of constraints defined by the
	 * actual state of the constraint stack and the passed index argument. If
	 * such a solution can be found it will be stored into a solution cache and
	 * will be reused if the same request will appear again later.
	 * 
	 * @param idx
	 *            the index of the system of constraints a solution should be
	 *            generated for.
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
	private Solution getSolution(int idx) throws SolverUnableToDecideException,
			TimeoutException {
		long startTime = System.nanoTime();

		throw new NotImplementedException();

		/*
		// try to find solution in the cache
		Solution solution = constraintStack.getCachedSolution(idx);
		if (solution != null) {
			listeners.fireInternalGetSolutionStarted(this, idx, null, true);
			listeners.fireInternalGetSolutionFinished(this, idx, solution,
					System.nanoTime() - startTime);
			return solution;
		}

		// otherwise calculate it
		ConstraintSystem system = constraintStack.getSystem(idx);
		listeners.fireInternalGetSolutionStarted(this, idx, system, false);
		if (system.isContradictory()) {
			solution = Solution.NOSOLUTION;
		} else {
			try {
				// a system has several variable-disjunct constraint sets
				// thus each one is solved independently and solutions are
				// joined afterwards
				// to form a solution for the whole conjunctive constraint
				// system
				for (int i = 0; i < system.getConstraintSetCount(); i++) {

					SingleConstraintSet constraintSet = system
							.getConstraintSet(i);
					Solution newSolution = null;

					// if constraint set consists only of one assignment we
					// already have solution
					if (constraintSet.getConstraintCount() == 1) {
						SingleConstraint constraint = constraintSet
								.getConstraint(0);
						if (constraint instanceof Assignment) {
							Assignment assignment = (Assignment) constraint;
							newSolution = new Solution();
							newSolution.addBinding(assignment.getVariable(),
									assignment.getValue());
						}
					}

					// otherwise try every solver that is able to handle the
					// constraint set
					// and take the first solution found
					if (newSolution == null) {
						Solver[] solver = getSolver(constraintSet);
						for (int solverNo = 0; solverNo < solver.length; solverNo++) {
							if (newSolution != null)
								break;

							listeners.fireSolverGetSolutionStarted(this,
									solver[solverNo], constraintSet);

							// if the last solver is unable to find a solution
							// an exception is thrown
							try {
								for (SingleConstraint constraint : constraintSet)
									solver[solverNo].addConstraint(constraint);
								newSolution = solver[solverNo].getSolution();
							} catch (SolverUnableToDecideException sutde) {
								if (solverNo == solver.length - 1)
									throw sutde;
							} catch (TimeoutException te) {
								if (solverNo == solver.length - 1)
									throw te;
							}

							listeners.fireSolverGetSolutionFinished(this,
									solver[solverNo], constraintSet,
									newSolution, System.nanoTime() - startTime);
						}
					}

					// join the sub solutions found so far
					if (solution == null)
						// if we are in the first iteration i.e. i==0 we start
						// with a new solution
						solution = newSolution;
					else
						// otherwise we just join the new found solution with
						// the previous one
						solution = solution.join(newSolution);

					// if one ConstraintSet is contradictory then the whole
					// system has no solution
					// and we can stop here
					if (solution.equals(Solution.NOSOLUTION)) {
						break;
					}
				}
			} catch (IncorrectSolverException ise) {
				ise.printStackTrace();
				return null;
			}
		}

		constraintStack.setSolution(idx, solution);
		listeners.fireInternalGetSolutionFinished(this, idx, solution,
				System.nanoTime() - startTime);
		return solution;*/
	}

	private Solver[] getSolver(SingleConstraintSet constraintSet) {
		throw new NotImplementedException();
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

		totalConstraintsChecked++; // Added 2008.05.02
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
	 * Checks whether a solution exists for the system of constraints stored in
	 * the actual constraint stack at the position idx by using the hasSolution
	 * methods of the constraint solvers. This method may be a little bit faster
	 * when only an assertion about the solvability of a system of constraints
	 * should be calculated.
	 * 
	 * @param idx
	 *            the index of the system of constraints the information about
	 *            the existence of a solution should be calculated for.
	 * @return <i>true</i> if any solution for the given problem exists,
	 *         <i>false</i> if definitively no solution satisfies the
	 *         constraints.
	 * @throws SolverUnableToDecideException
	 *             if the used solvers are not able to decide whether a solution
	 *             exists or not or are not able to find an existing solution
	 *             instance.
	 * @throws TimeoutException
	 *             if the used algorithms stops because of reaching the
	 *             specified time limits before being able to decide about the
	 *             given problem.
	 */

	private boolean hasSolution(int idx) throws SolverUnableToDecideException,
			TimeoutException {
		
		long startTime = System.nanoTime();
		
		throw new NotImplementedException();

		/* TODO caching
		 * Solution solutionTmp = constraintStack.getCachedSolution(idx);
		if (solutionTmp != null) {
			listeners.fireInternalHasSolutionStarted(this, idx, null, true);
			boolean result = !solutionTmp.equals(Solution.NOSOLUTION);
			listeners.fireInternalHasSolutionFinished(this, idx, result,
					System.nanoTime() - startTime);
			return result;
		}
		 */

		/*ConstraintSystem system = constraintStack.getSystem(idx);
		listeners.fireInternalHasSolutionStarted(this, idx, system, false);
		if (system.isContradictory()) {
			constraintStack.setSolution(idx, Solution.NOSOLUTION);
			listeners.fireInternalHasSolutionFinished(this, idx, false,
					System.nanoTime() - startTime);
			return false;
		} else {
			boolean result = true;
			try {
				for (int i = 0; i < system.getConstraintSetCount(); i++) {
					SingleConstraintSet constraintSet = system
							.getConstraintSet(i);

					if ((constraintSet.getConstraintCount() != 1)
							|| !(constraintSet.getConstraint(0) instanceof Assignment)) {
						// if there is at least one non assignment constraint
						// try to solve the set

						Solver[] solver = getSolver(constraintSet);
						for (int solverNo = 0; solverNo < solver.length
								&& result; solverNo++) {
							HasSolutionInformation intermediateResult = null;
							try {
								listeners.fireSolverHasSolutionStarted(this,
										solver[solverNo], constraintSet);
								for (SingleConstraint constraint : constraintSet)
									solver[solverNo].addConstraint(constraint);
								intermediateResult = solver[solverNo]
										.hasSolution();
								if (intermediateResult.hasSolution()) {
									// solution for this constraint set is
									// present -> skip to next set
									break;
								} else {
									result = false;
								}
							} catch (SolverUnableToDecideException sutde) {
								if (solverNo == solver.length - 1)
									throw sutde;
							} catch (TimeoutException te) {
								if (solverNo == solver.length - 1)
									throw te;
							} finally {
								listeners
										.fireSolverHasSolutionFinished(
												this,
												solver[solverNo],
												(intermediateResult != null) ? intermediateResult
														.hasSolution() : false,
												System.nanoTime() - startTime);
							}
						}
					}

					if (result == false) {
						constraintStack.setSolution(idx, Solution.NOSOLUTION);
						listeners.fireInternalHasSolutionFinished(this, idx,
								false, System.nanoTime() - startTime);
						return false;
					}
				}
			} catch (IncorrectSolverException ise) {
				ise.printStackTrace();
				return false;
			}
		}
		listeners.fireInternalHasSolutionFinished(this, idx, true,
				System.nanoTime() - startTime);
		return true;
		*/
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

	private void removeConstraints(int count) {
		if (logger.isDebugEnabled())
			logger.debug("Remove constraints " + count);
		
		if (count > 0) {
			int targetLevel = jacopStore.level - count;
			if (targetLevel < 0) targetLevel = 0;
			
			while (jacopStore.level > targetLevel) {
				jacopStore.removeLevel(jacopStore.level);
				jacopStore.setLevel(jacopStore.level - 1);
			}
			listeners.fireConstraintsRemoved(this, count);
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
		return totalConstraintsChecked;
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