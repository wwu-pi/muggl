package de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements;

import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.FreeArrayref;

/**
 * This TrailElement is the counterpiece for the xastore instructions. It is generated
 * when an array is changed. This can only happen when one of the following instructions
 * is executed: aastore, bastore, castore, dastore, fastore, iastore, lastore, sastore.
 * It takes the information which index of what arrayref has to be restored, and what
 * its original value was.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2008-11-24
 */
public class ArrayRestore extends Restore {
	// Field for the array reference.
	private final Arrayref arrayref;
	private final Term index;
	/**
	 * Constructor for reference arrays: Initialize with the array reference, the index
	 * and the value to restore.
	 * @param arrayref The array reference.
	 * @param index An index into the local variables.
	 * @param object The object that has to be restored at the local variable index.
	 */
	public ArrayRestore(Arrayref arrayref, int index, Object object) {
		super(index, object);
		this.arrayref = arrayref;
		this.index = IntConstant.getInstance(index);
	}

	public ArrayRestore(Arrayref arrayref, Term index, Object object) {
		super(-1, object);
		this.arrayref = arrayref;
		this.index = index;
	}


    public ArrayRestore createInverse() {
	    ArrayRestore inverse;
	    if (this.arrayref != null) {
			Object formerValue;
	    	if (arrayref instanceof FreeArrayref) {
	    		formerValue = ((FreeArrayref) arrayref).getFreeArrayElement(index);
			} else {
				formerValue = this.arrayref.getElement(((IntConstant) index).getIntValue());
			}
            inverse = new ArrayRestore(this.arrayref, this.index, formerValue);
        } else {
	        throw new IllegalStateException("Not allowed.");
        }
        return inverse;
    }

	/**
	 * Restore the array element saved by this class.
	 */
	public void restore() {
		if (this.arrayref != null) {
			if (arrayref instanceof FreeArrayref) {
				((FreeArrayref) arrayref).putElementIntoFreeArray(index, value);
			} else {
				arrayref.putElement(((IntConstant) index).getIntValue(), value);
			}
		} else {
			throw new IllegalStateException("Not allowed.");
		}
	}

	/**
	 * Restore the array element saved by this class.
	 *
	 * @param frame This parameter will be ignored.
	 */
	@Override
	public void restore(Frame frame) {
		restore();
	}

	/**
	 * Returns a suitable String representation of the trail element.
	 *
	 * @return A String representation of the trail element.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String valueInfo;
		if (this.value == null) {
			valueInfo = "The value is a null reference.";
		} else {
			valueInfo = "The value is of type " + this.value.getClass().getName()
					+ " and its toString() method returns: " + this.value.toString();
		}
		return "ArrayRestore: " + index + " -> " + value; // "Trail element that restores an element of an array to its former value. "
				//+ "The element index is " + this.index + "." + valueInfo;
	}
}
