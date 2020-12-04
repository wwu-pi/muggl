package de.wwu.muggl.vm.initialization;

import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.vm.VirtualMachine;
import sun.misc.VM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FreeArrayref extends ModifieableArrayref {
    public class UninitializedMarker {}
    private final Term lengthTerm;
    private final Map<Term, Object> elements;
    private final String name;

    public FreeArrayref(String name, ReferenceValue referenceValue, Term length) {
        super(referenceValue, 0);
        this.name = name + "_" + this.getArrayrefId();
        this.lengthTerm = length;
        this.elements = new HashMap<>();
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

    public Object getFreeArrayElement(int index) {
        return getFreeArrayElement(IntConstant.getInstance(index));
    }

    public void putElementIntoFreeArray(Term index, Object element) {
        if (element instanceof UninitializedMarker) {
            elements.remove(index);
        } else {
            elements.put(index, element);
        }
    }

    @Override
    public Object getElement(int index) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void putElement(int index, Object element) {
        throw new UnsupportedOperationException("Not supported.");
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
        throw new UnsupportedOperationException("Not supported.");
    }
}