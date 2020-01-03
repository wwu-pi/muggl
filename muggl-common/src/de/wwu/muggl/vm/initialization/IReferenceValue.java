package de.wwu.muggl.vm.initialization;

/**
 * Dummy interface. It is used as a common denominator for
 * [muggl-solvers]ClassConstraintExpression and [muggl-core]* in order to
 * avoid creating circular dependencies.
 */
public interface IReferenceValue {
    /**
     * This method is used in context with free objects in order to find out whether it is already a specific object.
     * For an Objectref and an Arrayref, it is always true as it is not a Free Object. FreeObjectref (and, in the future, further Free types) override this
     * and may provide a more nuanced implementation.
     * @return true if the represented object is of a specific type.
     */
    boolean isOfASpecificType();
}
