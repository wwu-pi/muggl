package de.wwu.muggl.solvers.expressions;

import de.wwu.muggl.vm.initialization.IReferenceValue;

/**
 * Marker type for constraints that operate on types.
 */
public interface TypeConstraint {
    IReferenceValue getTarget();
}
