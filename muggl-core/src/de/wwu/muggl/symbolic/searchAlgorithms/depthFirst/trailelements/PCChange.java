package de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements;

/**
 * This TrailElement indicates that the current PC has changed and can be used to set the PC
 * previously executed as the current one.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2008-08-29
 */
public class PCChange implements TrailElement {

	/**
	 * The stored pc
	 */
	private int	pc;

	/**
	 * Initialize with the pc.
	 *
	 * @param pc The previous pc.
	 */
	public PCChange(int pc) {
		this.pc = pc;
	}

	/**
	 * Getter for the pc.
	 *
	 * @return The previous pc.
	 */
	public int getPC() {
		return this.pc;
	}

	/**
	 * Returns a suitable String representation of the trail element.
	 *
	 * @return A String representation of the trail element.
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Trail element that changes the currently executed pc back to the stored pc %d",
				this.pc);
	}

}
