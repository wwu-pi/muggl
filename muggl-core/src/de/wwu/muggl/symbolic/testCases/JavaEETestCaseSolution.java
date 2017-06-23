package de.wwu.muggl.symbolic.testCases;

import java.util.Map;
import java.util.Set;

import de.wwu.muggl.javaee.jpa.SymbolicDatabase;
import de.wwu.muggl.javaee.testcase.obj.ObjectBuilder;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * The test case solution for a Java EE application.
 * 
 * @author Andreas Fuchs
 */
public class JavaEETestCaseSolution extends TestCaseSolution {
	
	protected ObjectBuilder objectBuilder;
	
	protected SymbolicDatabase symbolicDatabase;
	
	// the string builder for the pre-execution required database state
	protected StringBuilder dbPre;
	
	public JavaEETestCaseSolution(Method initialMethod, Solution solution, Object returnValue,
			SymbolicDatabase symbolicDatabase, boolean throwsAnUncaughtException,
			Object[] variables, boolean[] dUCoverage, Map<Method, boolean[]> cFCoverageMapping) {
		super(initialMethod, solution, returnValue, throwsAnUncaughtException, variables, dUCoverage, cFCoverageMapping);
		this.symbolicDatabase = symbolicDatabase;
		init();
	}

	public JavaEETestCaseSolution(Method initialMethod, Solution solution, Object returnValue,
			SymbolicDatabase symbolicDatabase, boolean throwsAnUncaughtException,
			Object[] variables, boolean[] dUCoverage, Map<Method, boolean[]> cFCoverageMapping,
			TestCaseSolution latestSolutionFound) {
		super(initialMethod, solution, returnValue, throwsAnUncaughtException, variables, dUCoverage, cFCoverageMapping, latestSolutionFound);
		this.symbolicDatabase = symbolicDatabase;
		init();
	}

	private void init() {
		this.objectBuilder = new ObjectBuilder(solution);
		this.dbPre = new StringBuilder();
		buildPreExecutionRequiredDatabase();
	}
	
	private void buildPreExecutionRequiredDatabase() {
		dbPre.append("\t\t// pre-execution required data\n");
		boolean emptyPreReqData = true; // flag to indicate that no pre execution required data exists
		Map<String, Set<Objectref>> data = symbolicDatabase.getPreExecutionRequiredData();
		for(String entityName : data.keySet()) {
			for(Objectref objRef : data.get(entityName)) {
				dbPre.append("\t\t// generate data for: " + objRef);
				emptyPreReqData = false;
			}
		}
		if(emptyPreReqData) {
			dbPre.append("\t\t// no pre-execution data required for this test case");
		}
		dbPre.append("\n");
	}

	public String getPreExecutionRequiredDatabaseString() {
		return this.dbPre.toString();
	}
}
