package de.wwu.muggl.vm.initialization;

import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.loading.MugglClassLoader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an Objectref, but for a Free Object, i.e. one that is used as a logic variable.
 */
public class FreeObjectref extends Objectref {

    /**
     * The set of types that the FreeObjectref may assume. If the set is singular, the FreeObjectref should reduce to an Objectref.
     * If it is empty, there is no valid type that the Object may take -- meaning that the FreeObjectref has been rendered infeasible by
     * some constraint.
     */
    private Set<String> possibleTypes;
    /**
     * The set of types that the FreeObjectref may NOT assume. Usually elements are subtypes of possibleTypes, otherwise they are of no relevance.
     */
    private Set<String> disallowedTypes;

    /**
     * During re-initialization of new types older initialization which are already involved in constraints might get lost.
     * This map thus memorizes already initialized fields of subclasses.
     */
    private Map<Field, Object> memorizedVariables = new HashMap<>();

    private Map<Field, FreeObjectrefInitialisers.LAZY_FIELD_MARKER> substitutedMarkers = new HashMap<>();;

    /**
     * Private constructor to get concrete instances of an initialized class. These instances
     * have a reference to the InitializedClass, which keeps control of the static
     * fields. The concrete instance itself keeps control of instance fields.
     *
     * @param staticReference  The InitializedClass (which has been generated by the ClassFile).
     * @param primitiveWrapper Toggles the usage of the Objectref as a wrapper for primitive types.
     */
    public FreeObjectref(InitializedClass staticReference, boolean primitiveWrapper) {
        super(staticReference, primitiveWrapper);


        // Discover all subtypes of the given type.
        // TODO Expand towards entire(!) class path!!
        // Extract list of classes first, because iterating over the classloader will modify classloader state.
        MugglClassLoader classLoader = staticReference.getClassFile().getClassLoader();
        List<ClassFile> loadedClasses = new ArrayList<>(classLoader.getLoadedClasses().values());
        possibleTypes = loadedClasses.stream()
                .filter(type -> type.isSubtypeOf(staticReference.getClassFile()))
                .map(type -> type.getName())
                .collect(Collectors.toSet());

        disallowedTypes = new HashSet<>();
    }

    public FreeObjectref(FreeObjectref other) {
        this(other.getInitializedClass(), other.isPrimitive());
        possibleTypes = new HashSet<>(other.getPossibleTypes());
        disallowedTypes = new HashSet<>(other.getDisallowedTypes());
        fields = new HashMap<>(other.getFields());
        memorizedVariables = new HashMap<>(other.memorizedVariables);
        substitutedMarkers = new HashMap<>(other.substitutedMarkers);
    }

    @Override
    public boolean isOfASpecificType() {
        // This becomes true if the type is sufficiently constrained.
        HashSet<String> types = new HashSet<>(possibleTypes);
        types.removeAll(disallowedTypes);
        return types.size() == 1;
    }

    @Override
    public Set<String> getPossibleTypes() {
        return possibleTypes;
    }

    @Override
    public List<Field> applyTypeConstraint(Set<String> possibleTypes, Set<String> disallowedTypes) {
        // Take possibleTypes and excludedTypes at the same time. Take the difference set.
        this.possibleTypes = possibleTypes;
        this.disallowedTypes = disallowedTypes;


        // Check whether the allowed types (possible \ disallowed) are along a single hierarchy. If so, use the common supertype for binding fields.
        HashSet<String> allowedTypes = new HashSet<>(possibleTypes);
        allowedTypes.removeAll(disallowedTypes);
        List<ClassFile> allowedTypeClasses = allowedTypes.stream().map(typeName -> {
            try {
                return this.getInitializedClass().getClassFile().getClassLoader().getClassAsClassFile(typeName);
            } catch (ClassFileException e) {
                throw new IllegalStateException(e);
            }
        }).collect(Collectors.toList());
        List<ClassFile> commonSupertypes = allowedTypeClasses.stream().filter(superType ->
                allowedTypeClasses.stream().allMatch(subType -> subType.isSubtypeOf(superType))).collect(Collectors.toList());

        // If there is a unique supertype, feel free to initialize.
        List<Field> boundFields = new ArrayList<>();
        if (commonSupertypes.size() == 1) {
            initializeNewBoundFields(commonSupertypes.get(0), boundFields);

        }
        return boundFields;
    }

    private void initializeNewBoundFields(ClassFile actualType, List<Field> boundFields) {
        while (actualType != null) {
            // Check which fields are annotated and replace undefined fields by logic variables.
            for (Field field : actualType.getFields()) {
                if (!this.hasValueFor(field)) {
                    // TODO Why is this done multiple times? I assume that for A <- B <- C this is done for a field b
                    //  for B and C separately. However, this destroys the identity of variables which might already be
                    //  involved in constraints. I will quick-fix this with a memorization-map for now...This can also be used
                    //  to get the original initialized values.
                    Object cachedValue = memorizedVariables.get(field);
                    if (cachedValue != null) {
                        fields.put(field, cachedValue);
                        boundFields.add(field);
                        continue;
                    }
                    String type = field.getDescriptor();
                    SearchingVM vm = (SearchingVM) (VirtualMachine.getLatestVM());
                    Object value = FreeObjectrefInitialisers.initializeLazyMarker(this, field);
                    memorizedVariables.put(field, value);
                    if (value != null) {
                        this.fields.put(field, value);
                        boundFields.add(field);
                    }
                }
            }
            try {
                actualType = actualType.getSuperClassFile();
            } catch (ClassFileException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void addSubstitutedLazyMarker(FreeObjectrefInitialisers.LAZY_FIELD_MARKER marker) {
        if (marker.initForObjectref != this) {
            throw new IllegalStateException("The marker should be added for the represented FreeObjectref.");
        }
        substitutedMarkers.put(marker.initForField, marker);
    }

    public FreeObjectrefInitialisers.LAZY_FIELD_MARKER getSubstitutedMarker(Field f) {
        return substitutedMarkers.get(f);
    }

    public Map<Field, FreeObjectrefInitialisers.LAZY_FIELD_MARKER> getSubstitutedMarkers() {
        return substitutedMarkers;
    }

    public Map<Field, Object> getMemorizedVariables() {
        return memorizedVariables;
    }

    @Override
    public Set<String> getDisallowedTypes() {
        return disallowedTypes;
    }

    /**
     * Unbind fields that were previously initialised via #setPossibleTypes().
     * @param fieldsToUnbind
     */
    @Override
    public void unbindFields(List fieldsToUnbind) {
        List<Field> fields = (List<Field>) fieldsToUnbind;
        for (Field field : fields) {
            this.fields.remove(field);
        }
    }

    @Override
    public String toString() {
        return "Free" + super.toString();
    }

    @Override
    public Objectref getMirrorJava() {
        if (this.possibleTypes.size() == 1) {
            String actualType = this.possibleTypes.stream().findFirst().get();
            try {
                return this.getInitializedClass().getClassFile().getClassLoader().getClassAsClassFile(actualType).getMirrorJava();
            } catch (ClassFileException e) {
                throw new IllegalStateException(e);
            }
        }
        return super.getMirrorJava();
    }

    public void setConcreteStaticReference(InitializedClass concreteClass) {
        this.staticReference = concreteClass;
    }
}
