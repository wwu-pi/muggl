package de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements;

import de.wwu.muggl.vm.Frame;

/**
 * This TrailElement indicates that the topmost element of the operand stack of the specified frame
 * has to be popped.
 *
 * TODO change description
 *
 * Only used as the inverse to PopFromFrame, i.e. not created during normal symbolic execution, but very important for inverse trails.
 *
 * @author Jan C. Dageförde
 */
public class PushToFrame implements TrailElement {
    private final Object value;
    private Frame frame;

	/**
	 * Construct the trail element.
	 *
	 * @param frame The frame to push an element to.
     * @param value Element to be pushed.
	 */
	public PushToFrame(Frame frame, Object value) {
		this.frame = frame;
		this.value = value;
	}

	/**
	 * Returns a suitable String representation of the trail element.
	 *
	 * @return A String representation of the trail element.
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Trail element that pushes the object " + this.value.toString() + " to the operand stack of the specified frame ("
				+ this.frame.toString() + ").";
	}

    /**
     * Getter for the frame to which an element shall be pushed.
     *
     * @return The frame to which an element shall be pushed.
     */
    public Frame getFrame() {
        return this.frame;
    }

    /**
     * Getter for the value of the element that shall be pushed.
     *
     * @return The value of the element that shall be pushed.
     */
    public Object getValue() {
        return this.value;
    }

}
