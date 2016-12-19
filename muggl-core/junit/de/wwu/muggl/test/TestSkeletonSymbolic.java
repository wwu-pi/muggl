package de.wwu.muggl.test;

import org.junit.BeforeClass;
import de.wwu.muggl.configuration.Options;

public class TestSkeletonSymbolic extends TestSkeleton {

	@BeforeClass
	public static void setUpBeforeClass2() throws Exception {
		Options.getInst().symbolicMode = true;
		Options.getInst().logicMode = false;
		Options.getInst().testClassesDirectory = "/tmp";
	}

}
