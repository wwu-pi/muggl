package de.wwu.muggl.vm.initialization;

import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.vm.VirtualMachine;
import sun.misc.VM;

import java.util.*;
import java.util.function.DoubleBinaryOperator;

public class FreeArrayref extends ModifieableArrayref {
    public class UninitializedMarker {}
    private Term lengthTerm;
    private Map<Term, Object> elements;
    private final String name;
    private boolean concretized;
    // If a program explicitly stores values in the FreeArray (that means: they are not initialized free by
    // retrieving a value using load), the corresponding indices and values are stored here.
    protected Map<Term, Object> originalElements;

    public FreeArrayref(FreeArrayref other) {
        super(other);
        name = other.getName();
        lengthTerm = other.getLengthTerm();
        elements = new HashMap<>(other.getFreeArrayElements());
        originalElements = new HashMap<>(other.originalElements);
        concretized = other.concretized;
        representedTypeIsAPrimitiveWrapper = other.isRepresentedTypeIsAPrimitiveWrapper();
    }

    public FreeArrayref(String name, ReferenceValue referenceValue, Term length) {
        super(referenceValue, 0);
        this.name = name + "_" + this.getArrayrefId();
        this.lengthTerm = length;
        this.elements = new HashMap<>();
        originalElements = new HashMap<>();
        if (concretized) {
            if (!(lengthTerm instanceof IntConstant)) {
                throw new IllegalStateException("Concretized free arrays should have constant length");
            }
        }
    }

    public void concretizeWith(ArrayList<Object> values, IntConstant length) {
        if (values.size() != length.getIntValue()) {
            throw new IllegalStateException("Number of elements and length must equal.");
        }
        concretized = true;
        elements = new HashMap<>();
        lengthTerm = length;
        for (int i = 0; i < values.size(); i++) {
            putElement(i, values.get(i));
        }
        // TODO Change referenceValue if term?
    }

    public Term getLengthTerm() {
        return lengthTerm;
    }

    public String getVarNameWithId() {
        return name;
    }

    public Map<Term, Object> getFreeArrayElements() {
        return elements;
    }
    
    public Object getFreeArrayElement(Term index) {
        Object result = elements.get(index);
        if (result == null && !elements.containsKey(index)) {
            return new UninitializedMarker();
        }
        return result;
    }

    public void putElementIntoFreeArray(Term index, Object element, boolean freeInitialized) {
        // Is the element initialized due to an Aload?
        if (freeInitialized && !originalElements.containsKey(index)) {
            originalElements.put(index, element);
        }
        if (element instanceof UninitializedMarker) {
            elements.remove(index);
        } else {
            elements.put(index, element);
        }
    }

    public void putElementIntoFreeArray(Term index, Object element) {
        putElementIntoFreeArray(index, element, false);
    }

    @Override
    public Object getElement(int index) {
        if (concretized) {
            Object value = elements.get(IntConstant.getInstance(index));
            if (value == null && referenceValue.isPrimitive()) {
                if (referenceValue.getName().equals(Integer.class.getName())) {
                    return 0;
                } else if (referenceValue.getName().equals(Double.class.getName())) {
                    return 0.0;
                } else {
                    throw new IllegalStateException("Not yet implemented.");
                }
            } else {
                return value;
            }
        } else {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    @Override
    public void putElement(int index, Object element) {
        if (concretized) {
            // TODO Check element
            elements.put(IntConstant.getInstance(index), element);
        } else {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    public Map<Term, Object> getOriginalElements() {
        return originalElements;
    }

    @Override
    public int[] getDimensions() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public byte[] getElements() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Object[] getRawElements() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void reset(int length) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void disableTypeChecking() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public FreeArrayref clone() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int[] toPrimitiveIntFlat() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getSignature() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String toString() {
        return "FreeArrayref{name=" + getVarNameWithId() + ",elements=" + elements + ",length=" + lengthTerm + "}";
    }

    @Override
    public int getLength() {
        if (concretized) {
            return ((IntConstant) lengthTerm).getIntValue();
        } else {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    public void setFreeArrayElements(Map<Term, Object> newElements) {
        this.elements = newElements;
    }

    public void setLengthTerm(Term newLengthTerm) {
        this.lengthTerm = newLengthTerm;
    }
}