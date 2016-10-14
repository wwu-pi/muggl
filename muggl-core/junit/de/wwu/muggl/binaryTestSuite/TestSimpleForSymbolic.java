package de.wwu.muggl.binaryTestSuite;

public class TestSimpleForSymbolic {
	public int olderThanSammy(int myAge) {
		int sammysAge = 25;

		if (myAge < sammysAge) {
			return -1;
		} else if (myAge == sammysAge) {
			return 0;
		} else {
			return 1;
		}
	}
}
