package de.wwu.muggl.vm.initialization;

import de.wwu.muggl.vm.SystemDictionary;
import de.wwu.muggl.vm.execution.ExecutionAlgorithms;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.solvers.expressions.Term;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a reference to an array. It stores information about
 * the array type, its length and its element. It also offers access to the
 * elements stored.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2011-01-15
 */
public class Arrayref implements ReferenceValue {
	// Fields.
	/**
	 * The corresponding reference value of the array reference.
	 */
	protected ReferenceValue referenceValue;
	/**
	 * The length (number of elements) of the array referenced.
	 */
	public int length;
	/**
	 * The elements stored in this array.
	 */
	protected Object[] elements;
	private long instantiationNumber;
	
	private Objectref mirrorJava;

	/**
	 * Initialize the arrayref. It must have a type of ReferenceValue and
	 * a fixed length.
	 *
	 * @param referenceValue The desired ReferenceValue.
	 * @param length The desired length.
	 */
	public Arrayref(ReferenceValue referenceValue, int length) {
		this.referenceValue = referenceValue;
		this.instantiationNumber = referenceValue.getInitializedClass().getClassFile()
		.getClassLoader().getNextInstantiationNumber();
		this.length = length;
		if (referenceValue.isPrimitive()) {
			// Primitive types will be represented in the java.lang wrapper classes.
			this.elements = new Object[this.length];
			initializePrimitiveWrapperValues();
		} else {
			this.elements = new ReferenceValue[this.length];
		}
		
		setupMirror();
		
	}

	private void setupMirror() {
		if(SystemDictionary.isInitialized() &&
				SystemDictionary.gI().Class_klass_loaded()){
			Objectref instanceMirror = SystemDictionary.gI().Class_klass.getANewInstance();
			instanceMirror.setMirroredMugglIsArray(true);
			instanceMirror.setMirroredMugglArray(this);
			this.mirrorJava = instanceMirror;
		}
		Globals.getInst().execLogger.trace("created arrayref id:" + this.instantiationNumber);
	}

	/**
	 * Recursively generate a multidimensional array reference.
	 *
	 * @param referenceValue The reference value for the array.
	 * @param dimensionCount The length of the arrays' dimensions.
	 */
	public Arrayref(
			ReferenceValue referenceValue,
			int[] dimensionCount
			) {
		this(referenceValue, dimensionCount, 0);
	}

	/**
	 * Recursively generate a multidimensional array reference.
	 *
	 * @param referenceValue The reference value for the array.
	 * @param dimensionCount The length of the arrays' dimensions.
	 * @param dimension The currently processed dimension.
	 */
	private Arrayref(
			ReferenceValue referenceValue,
			int[] dimensionCount,
			int dimension
			) {
		this.instantiationNumber = referenceValue.getInitializedClass().getClassFile()
		.getClassLoader().getNextInstantiationNumber();
		if (dimensionCount[0] == 0) {
			this.referenceValue = referenceValue;
			this.length = 0;
		} else {
			for (int a = 0; a < dimensionCount[dimension]; a++) {
				Arrayref value = null;
				if (dimensionCount.length > dimension + 1) {
					// Generate the "deeper" dimension.
					value = new Arrayref(referenceValue, dimensionCount, dimension + 1);
					// Generate the array reference on the first pass.
					if (a == 0) {
						this.referenceValue = value;
						this.length = dimensionCount[dimension];
						this.elements = new ReferenceValue[this.length];
					}
					// Fill in the values.
					putElement(a, value);
				} else {
					// Generate the array reference on the first pass.
					this.referenceValue = referenceValue;
					this.length = dimensionCount[dimension];
					if (referenceValue.isPrimitive()) {
						// Primitive types will be represented in the java.lang wrapper classes.
						this.elements = new Object[this.length];
						initializePrimitiveWrapperValues();
					} else {
						this.elements = new ReferenceValue[this.length];
					}
				}
			}
		}
		setupMirror();
	}

	/**
	 * Get an element from this arrayref.
	 * 
	 * @param index The index of the element to get.
	 * @return The element at the specified index.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of the array references bounds.
	 */
	public Object getElement(int index) {
		if (index < 0 || index >= this.length) 
			throw new ArrayIndexOutOfBoundsException("Array index[" + index + "] out of bounds inst.nr:" + this.getInstantiationNumber());
		return this.elements[index];
	}

	/**
	 * Put an element to this arrayref.
	 * 
	 * @param index The index to put the element to.
	 * @param element The element to be put.
	 * @throws ArrayStoreException If the element to store is not assignment compatible with the
	 *         arrayref.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of the Arrayref's bounds.
	 */
	public void putElement(int index, Object element) {
		if (index < 0 || index >= this.length)
			throw new ArrayIndexOutOfBoundsException("Array index out of bounds");

		ExecutionAlgorithms ea = new ExecutionAlgorithms(getInitializedClass().getClassFile()
				.getClassLoader());
		if (element instanceof Term) {
			// Skip the check!
			// TODO
		} else if (this.referenceValue.isPrimitive() && !(element instanceof ReferenceValue)) {
			String srcClass = element.getClass().getName();
			String targetClass = this.referenceValue.getInitializedClass().getClassFile().getName();
			if (!targetClass.equals(srcClass)) {
				// it is legal to store ints (from the stack, see baload/bastore in byte and booleans
				if (srcClass.equals("java.lang.Integer")) {
					if (targetClass.equals("java.lang.Byte")) {
						element = ((Integer) element).byteValue();
					} else if (targetClass.equals("java.lang.Boolean")) {
						element = (((Integer) element) == 0) ? false : true;
					}else if (targetClass.equals("java.lang.Character")) {
						element = (char)((int) element);
					}else if (targetClass.equals("java.lang.Short")) {
						element = ((Integer) element).shortValue();
					}				
				}
			}

			if (!this.referenceValue.getInitializedClass().getClassFile().getName()
					.equals(element.getClass().getName()))
				throw new ArrayStoreException(element.getClass().getName()
						+ " is not assignment compatible with a primitive wrapper provided by " + this.getName() + ".");
		} else {
			// Normal assignment compatibility check.
			if (!ea.checkForAssignmentCompatibility((ReferenceValue) element, this))
				throw new ArrayStoreException(((ReferenceValue) element).getName() + " 1is not assignment compatible with " + this.getName() + ".");
		}
		this.elements[index] = element;
	}

	/**
	 * Return true, since this is a reference to an array.
	 * @return true, since this is a reference to an array.
	 */
	public boolean isArray() {
		return true;
	}

	/**
	 * Return a String representation of the arrayref.
	 * @return A String representation of the arrayref.
	 */
	@Override
	public String toString() {
		String dbghint = "";
		if(getName().equals("[Ljava.lang.Character;")) {
			for (int i = 0; i < elements.length; i++) {
				dbghint += elements[i];
			}
		}
		return "Arrayref of type " + getName() + " length " + this.length + " (id: "
				+ this.instantiationNumber + ((dbghint.length() > 0) ? (" content:" + dbghint) : "") + ").";
	}

	/**
	 * Get the name of this reference value.
	 *
	 * @return The name of this reference value.
	 */
	public String getName() {
		String name = this.referenceValue.getName();
		if (name.contains("[L")) return "[" + name;
		return "[L" + name + ";";
	}

	/**
	 * Getter for the corresponding InitializedClass.
	 * @return The corresponding InitializedClass.
	 */
	public InitializedClass getInitializedClass() {
		return this.referenceValue.getInitializedClass();
	}

	/**
	 * Returns true, if the ReferenceValue is wrapping a primitive type.
	 * @return true, if the ReferenceValue is wrapping a primitive type, false otherwise.
	 */
	public boolean isPrimitive() {
		return this.referenceValue.isPrimitive();
	}

	/**
	 * Getter for the instantiation number. Instantiation numbers can be used
	 * to determine which of two ReferenceValues has been generated earlier.
	 * @return The instantiation number.
	 */
	public long getInstantiationNumber() {
		return this.instantiationNumber;
	}

	/**
	 * Getter for the ReferenceValue of this arrayref.
	 * @return The ReferenceValue of this arrayref.
	 */
	public ReferenceValue getReferenceValue() {
		return this.referenceValue;
	}

	/**
	 * Initialize primitive wrapper values. As primitive types do not take null values, their
	 * wrapper objects are initialized to the respective initial values for primitive types.
	 */
	private void initializePrimitiveWrapperValues() {
		// Get the zero value.
		String wrappedType = this.getReferenceValue().getInitializedClass().getClassFile().getName();
		Object zeroInitializationValue = StaticInitializationSupport
				.getInitializedPrimitiveTypeWrapper(wrappedType, true);

		// Set it for any null value.
		for (int a = 0; a < this.length; a++) {
			this.elements[a] = zeroInitializationValue;
		}
	}

	/**
	 * Clone this array reference. This will generate a new Arrayref and copy the elements
	 * into it. If the ReferenceValue of this Arrayref is an Arrayref itself i.e. this
	 * arrayref contains nested array references, these will be cloned recursively.<br />
	 * <br />
	 * Please note than Cloning should only be done with care. Cloning of large objects might
	 * be slow and memory consuming. If elements are not primitive but reference values,
	 * they need to be cloned manually or changing them in the cloned Arrayref will have them
	 * changed in the original Arrayref (as it is one object with two references on it then).
	 * 
	 * @return The cloned array reference.
	 */
	@Override
	public Arrayref clone() {
		Arrayref arrayref = new Arrayref(this.referenceValue, this.length);
		for (int a = 0; a < this.length; a++) {
			Object element = this.elements[a];
			if (this.referenceValue.isArray())
				element = ((Arrayref) element).clone();
			arrayref.putElement(a, element);
		}
		return arrayref;
	}

	/**
	 * Get the dimensions of this array reference. This method will not only return the lengths of
	 * the current dimension but an array showing the length of any dimension.
	 *
	 * @return The lengths of the array reference's dimensions.
	 */
	public int[] getDimensions() {
		int[] dimensions;
		if (this.referenceValue.isArray()) {
			// Recursive invocation.
			int[] furtherDimensions = ((Arrayref) this.referenceValue).getDimensions();
			dimensions = new int[furtherDimensions.length + 1];
			System.arraycopy(furtherDimensions, 0, dimensions, 1, furtherDimensions.length);
		} else {
			dimensions = new int[1];
		}

		// Add this dimension and return the result;
		dimensions[0] = this.length;
		return dimensions;
	}
	
	/**
	 * Return the Signature in the form of [[I and respect primitive types
	 * @return
	 */
	public String getSignature() {
		int levels = 1; // we have at minimum one level. Useless otherways
		Object[] elementes = this.elements;
		if (elementes != null) {
			for (int i = 0; i < elementes.length; i++) {
				if (elementes[i] instanceof Arrayref) {
					levels++;
					elementes = ((Arrayref) elementes[i]).elements;
				}
			}
		}
		String levelsBrackets = "";
		for (int i = 0; i < levels; i++) {
			levelsBrackets += "[";
		}

		return levelsBrackets + this.referenceValue.getSignature();
	}

	public Objectref getMirrorJava() {
		return mirrorJava;
	}

	/**
	 * convert back to one-dimensional int[] array. Does not do it recursively
	 * 
	 * @return
	 */
	public int[] toPrimitiveIntFlat() {
		int[] ret = new int[this.length];

		for (int i = 0; i < this.length; i++) {
			if (this.getElement(i) instanceof Arrayref) {
				// maybe in future use - but please test
				// int[] sub = ((Arrayref)
				// this.getElement(i)).toPrimitiveInt(); System.arraycopy(sub, 0, ret, i, sub.length);
			} else
				ret[i] = (int) this.getElement(i);
		}
		return ret;
	}
	
	public byte[] getElements() {
		byte[] ret = new byte[this.length];
		
		for (int i = 0; i < this.length; i++) {			
				ret[i] = (byte) this.getElement(i);
		}
		return ret;
	}

	public Object[] getRawElements() {
	    return this.elements;
    }

    @Override
    public boolean isOfASpecificType() {
        return true;
    }

    @Override
    public Set<String> getPossibleTypes() {
        HashSet<String> types = new HashSet<>();
        types.add(getSignature());
        return types;
    }
}
