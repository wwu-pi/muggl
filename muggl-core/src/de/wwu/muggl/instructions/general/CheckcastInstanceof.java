package de.wwu.muggl.instructions.general;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.initialization.FreeObjectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract instruction with some concrete methods for the instructions checkcast and instanceof.
 * The concrete instructions can be extended from this class.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2009-03-23
 */
public abstract class CheckcastInstanceof extends GeneralInstructionWithOtherBytes {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method that the
	 * instruction belongs to is supplied as an argument.
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized successfully, most likely due to missing additional bytes. This might be caused by a corrupt classfile, or a classfile of a more recent version than what can be handled.
	 */
	public CheckcastInstanceof(AttributeCode code) throws InvalidInstructionInitialisationException {
		super(code);
	}

	/**
	 * Get the number of other bytes for this instruction.
	 * @return The number of other bytes.
	 */
	@Override
	public int getNumberOfOtherBytes() {
		return 2;
	}


    /**
     * Generate the sets allowedTypes = (Possible \ Disallowed); successfulTypes: Where the cast to `castTarget' would succeed; adverseTypes: Where a cast would fail.
     * @param classLoader the current class loader
     * @param freeObject object under consideration
     * @param castTarget desired cast or type target
     * @return an object containing the three sets
     */
    protected FreeObjectTypeRelations findTypeRelations(MugglClassLoader classLoader, FreeObjectref freeObject, String castTarget) {
        // Create the set difference.
        Set<String> allowedTypes = new HashSet<>(freeObject.getPossibleTypes());
        allowedTypes.removeAll(freeObject.getDisallowedTypes());

        // Load class files from type name strings.
        ClassFile castTargetClass;
        try {
            castTargetClass = classLoader.getClassAsClassFile(castTarget);
        } catch (ClassFileException e) {
            throw new IllegalStateException(e);
        }
        List<ClassFile> allowedTypeClasses = allowedTypes.stream().map(possibleInstanceType -> {
            try {
                return classLoader.getClassAsClassFile(possibleInstanceType);
            } catch (ClassFileException e) {
                throw new IllegalStateException(e);
            }
        }).collect(Collectors.toList());

        // Find out whether there are any types in the set for which the cast would succeed if the object were of that type.
        Set<String> successfulTypes = allowedTypeClasses.stream().filter(possibleClass -> possibleClass.isSubtypeOf(castTargetClass)
        ).map(ClassFile::getName).collect(Collectors.toSet());

        // Find out whether there are any types in the set for which the cast would fail if the object were of that type.
        Set<String> adverseTypes = allowedTypeClasses.stream()
                .filter(possibleClass -> !possibleClass.isSubtypeOf(castTargetClass))
                .map(ClassFile::getName).collect(Collectors.toSet());
        return new FreeObjectTypeRelations(allowedTypes, successfulTypes, adverseTypes);
    }

    static protected class FreeObjectTypeRelations {
        private final Set<String> allowedTypes;
        private final Set<String> successfulTypes;
        private final Set<String> adverseTypes;

        public FreeObjectTypeRelations(Set<String> allowedTypes, Set<String> successfulTypes, Set<String> adverseTypes) {
            this.allowedTypes = allowedTypes;
            this.successfulTypes = successfulTypes;
            this.adverseTypes = adverseTypes;
        }

        public Set<String> getAllowedTypes() {
            return allowedTypes;
        }

        public Set<String> getSuccessfulTypes() {
            return successfulTypes;
        }

        public Set<String> getAdverseTypes() {
            return adverseTypes;
        }
    }
}
