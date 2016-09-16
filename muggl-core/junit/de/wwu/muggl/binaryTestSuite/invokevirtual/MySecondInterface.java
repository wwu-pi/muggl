/**
 * 
 */
package de.wwu.muggl.binaryTestSuite.invokevirtual;

/**
 * @author Max Schulze
 *
 */
public interface MySecondInterface extends java.io.Serializable, MyThirdInterface {
	default String defaultInMySecondInterface() {
		return "very cool";
	}
}
