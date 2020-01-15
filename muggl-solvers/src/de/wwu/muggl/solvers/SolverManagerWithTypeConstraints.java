package de.wwu.muggl.solvers;

import de.wwu.muggl.solvers.expressions.ClassConstraintExpression;
import de.wwu.muggl.solvers.expressions.TypeConstraint;
import de.wwu.muggl.vm.initialization.IReferenceValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class SolverManagerWithTypeConstraints {

    /**
     * Map of imposed type constraints. Contract: The element at typeConstraints[i] is either null, then there is a constraint at level i in the jacopStore.
     * If typeConstraints[i] is not null, the corresponding level on the jacopStore does not have a constraint.
     */
    private ArrayList<TypeConstraint> typeConstraints = new ArrayList<>();

    /**
     * Trail representing a FreeObjectref's possible types prior to rebinding.
     */
    private HashMap<TypeConstraint, Set<String>> trail = new HashMap<>();

    protected void imposeTypeConstraint(TypeConstraint ce) {
        this.typeConstraints.add(ce);

        if (ce == null) {
            // Our job here is done ;) We added null as a dummy constraint to keep up with other solvers' "level" terminology, adding a dummy level here.
            return;
        }

        // Apply effects on constrained FreeObjectref, depending on the type:
        if (ce instanceof ClassConstraintExpression) {
            imposeClassConstraint((ClassConstraintExpression)ce);
        }
    }

    private void imposeClassConstraint(ClassConstraintExpression ce) {
        IReferenceValue targetRef = ce.getTarget();
        Set<String> allowedByTarget = targetRef.getPossibleTypes();
        Set<String> allowedByConstraint = ce.getTypes();

        // Put previous binding on trail in order to restore it later on.
        trail.put(ce, allowedByTarget);

        // Create the intersection and apply it.
        Set<String> intersection = new HashSet<>(allowedByTarget);
        intersection.retainAll(allowedByConstraint);
        targetRef.setPossibleTypes(intersection);
    }


    protected boolean hasInconsistentTypeConstraints() {
        // Iterate over all affected FreeObjects to check whether any of the applied constraints rendered the type of a FreeObjectref invalid.
        return this.typeConstraints.stream().anyMatch(ce -> ce.getTarget().getPossibleTypes().isEmpty());
    }

    protected void removeTypeConstraint() {
        int oldLevel = this.typeConstraints.size() - 1;
        TypeConstraint formerConstraint = this.typeConstraints.get(oldLevel);
        this.typeConstraints.remove(oldLevel);

        if (formerConstraint == null) {
            // Nothing to do here. Dummy constraint level, see #addTypeConstraint().
            return;
        }

        // Revert effects on constrained FreeObjectref.
        Set<String> typesFromTrail = trail.remove(formerConstraint);
        formerConstraint.getTarget().setPossibleTypes(typesFromTrail);
    }
}
