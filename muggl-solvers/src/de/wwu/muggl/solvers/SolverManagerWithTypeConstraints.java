package de.wwu.muggl.solvers;

import de.wwu.muggl.solvers.expressions.ClassConstraintExpression;
import de.wwu.muggl.solvers.expressions.TypeConstraint;
import de.wwu.muggl.vm.initialization.IReferenceValue;

import java.util.*;

public abstract class SolverManagerWithTypeConstraints {

    /**
     * Map of imposed type constraints. Contract: The element at typeConstraints[i] is either null, then there is a constraint at level i in the jacopStore.
     * If typeConstraints[i] is not null, the corresponding level on the jacopStore does not have a constraint.
     */
    private ArrayList<TypeConstraint> typeConstraints = new ArrayList<>();

    /**
     * Trail representing a FreeObjectref's possible types prior to rebinding.
     */
    private HashMap<TypeConstraint, TypeBindingTrail> trail = new HashMap<>();

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

    class TypeBindingTrail {
        public List newlyBoundFields;
        public Set<String> previouslyPossibleTypes;
        public Set<String> previouslyDisallowedTypes;
    }
    private void imposeClassConstraint(ClassConstraintExpression ce) {
        IReferenceValue targetRef = ce.getTarget();
        Set<String> allowedByTarget = targetRef.getPossibleTypes();
        Set<String> disallowedByTarget = targetRef.getDisallowedTypes();
        Set<String> allowedByConstraint = ce.getTypes();

        // Create the reduced type set and apply the constraint.
        Set<String> afterApplication = new HashSet<>(allowedByTarget);
        afterApplication.retainAll(allowedByConstraint);
        afterApplication.removeAll(ce.getNotTypes());
        List boundFields = targetRef.setPossibleTypes(afterApplication);

        // Explicitly disallow (sub)types.
        Set<String> allDisallowedTypes = new HashSet<>(disallowedByTarget);
        allDisallowedTypes.addAll(ce.getNotTypes());
        targetRef.setDisallowedTypes(allDisallowedTypes);

        // Put previous binding on trail in order to be able to restore it later on.
        TypeBindingTrail typeBinding = new TypeBindingTrail();
        typeBinding.newlyBoundFields = boundFields;
        typeBinding.previouslyPossibleTypes = allowedByTarget;
        typeBinding.previouslyDisallowedTypes = disallowedByTarget;
        trail.put(ce, typeBinding);
    }


    protected boolean hasInconsistentTypeConstraints() {
        // Iterate over all affected FreeObjects to check whether any of the applied constraints rendered the type of a FreeObjectref invalid.
        return this.typeConstraints.stream().filter(ce -> ce != null).anyMatch(ce -> ce.getTarget().getPossibleTypes().isEmpty());
    }

    protected void removeTypeConstraint() {
        int oldLevel = this.typeConstraints.size() - 1;
        TypeConstraint formerConstraint = this.typeConstraints.get(oldLevel);
        this.typeConstraints.remove(oldLevel);

        if (formerConstraint == null) {
            // Nothing to do here. Dummy constraint level, see #addTypeConstraint().
            return;
        }

        // Use trail to revert effects on constrained FreeObjectref.
        TypeBindingTrail typesFromTrail = trail.remove(formerConstraint);
        formerConstraint.getTarget().unbindFields(typesFromTrail.newlyBoundFields);
        formerConstraint.getTarget().setPossibleTypes(typesFromTrail.previouslyPossibleTypes);
        formerConstraint.getTarget().setDisallowedTypes(typesFromTrail.previouslyDisallowedTypes);
    }
}
