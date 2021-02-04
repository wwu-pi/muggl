package de.wwu.muggl.vm.initialization;

import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;

public class FreeObjectrefInitialisers {

    // To avoid infinite loops due to cyclic dependencies between classes, we initialize the fields of a
    // free object with LAZY_FIELD_MARKER instances. These markers store the corresponding Objectref and the Field.
    // When executing getfield-bytecode on an Objectref, this marker will be substituted by calling
    // createRepresentationForFreeVariableOrField(...). The substituted value will be stored. This enables copies which
    // were created in SolutionIterator (to represent the inputs of a method-call) to get the initial values.
    public static final class LazyFieldMarker {
        public final FreeObjectref initForObjectref;
        public final Field initForField;
        private Object substituteFor = null;

        private LazyFieldMarker(FreeObjectref initForObjectref, Field initForField) {
            this.initForField = initForField;
            this.initForObjectref = initForObjectref;
        }

        private void setSubstituteFor(Object substituteFor) {
            this.substituteFor = substituteFor;
        }

        public Object getSubstituteFor() {
            return substituteFor;
        }

        private static LazyFieldMarker get(FreeObjectref initForObjectref, Field initForField) {
            return new LazyFieldMarker(initForObjectref, initForField);
        }

        public Object replaceLazyMarker() {
            SearchingVM vm = (SearchingVM) VirtualMachine.getLatestVM();
            Object value;
            if (substituteFor != null) {
                value = substituteFor;
            } else {
                value = FreeObjectrefInitialisers.createRepresentationForFreeVariableOrField(
                        vm,
                        vm.getCurrentFrame().getMethod().getClassFile(),
                        this.initForField.getDescriptor(),
                        this.initForField.getName()
                );
                this.setSubstituteFor(value);
                this.initForObjectref.addSubstitutedLazyMarker(this);
            }
            this.initForObjectref.putField(this.initForField, value);
            return value;
        }
    }

    public static Object createRepresentationForFreeVariableOrField(SearchingVM vm, ClassFile fromClass, String type, String name) {
        // Convert string type to expression type.
        Expression.Type expressionType = null;
        switch(type) {
        case "B":
            expressionType = Expression.Type.BYTE;
            break;
        case "C":
            expressionType = Expression.Type.CHAR;
            break;
        case "D":
            expressionType = Expression.Type.DOUBLE;
            break;
        case "I":
            expressionType = Expression.Type.INT;
            break;
        case "F":
            expressionType = Expression.Type.FLOAT;
            break;
        case "J":
            expressionType = Expression.Type.LONG;
            break;
        case "S":
            expressionType = Expression.Type.SHORT;
            break;
        case "Z":
            expressionType = Expression.Type.BOOLEAN;
            break;
        default:
            if (type.startsWith("L")) {
                expressionType = Expression.Type.OBJECT;
            } else if (type.startsWith("[")) {
                expressionType = Expression.Type.ARRAY;
            }
        }

        if (expressionType == null) {
            throw new IllegalStateException("Unexpected type '" + type + "' -- cannot use this in a free variable.");
        }

        Object freeVariableRepresentation;
        // Find the correct type and create an appropriate representation.
        if (expressionType == Expression.Type.OBJECT) {
            freeVariableRepresentation = FreeObjectrefInitialisers.createRepresentationForFreeObject(vm, fromClass, type);
        } else if (expressionType == Expression.Type.ARRAY) {
            freeVariableRepresentation = createRepresentationForFreeArray(vm, fromClass, type, name);
        } else if (expressionType == Expression.Type.BOOLEAN) {
            freeVariableRepresentation = new BooleanVariable(name);
        } else {
            freeVariableRepresentation = new NumericVariable(name, expressionType.toByte(), false);
        }
        return freeVariableRepresentation;
    }

    public static FreeObjectref createRepresentationForFreeObject(SearchingVM vm, ClassFile fromClass, String type) {

        ClassFile classFile = resolveOrThrowException(vm, fromClass, type);
        // Get an uninitialised(!) Objectref.
        FreeObjectref anObjectref = vm.getAFreeObjectref(classFile);

        // Do not call initialisers:
        // - the static one already ran (via getAnObjectref).
        // - the instance one is not called on purpose, see paper.

        for (Field field : classFile.getFields()) {
            if (field.getName().contains("$this0")) {
                continue;
            }
            if (anObjectref.hasValueFor(field)) {
                continue;
            }
            // Initialise uninitialised fields with lazy markers.
            anObjectref.putField(field, LazyFieldMarker.get(anObjectref, field));
        }
        return anObjectref;
    }

    public static LazyFieldMarker initializeLazyMarker(FreeObjectref freeObjectref, Field field) {
        LazyFieldMarker result = LazyFieldMarker.get(freeObjectref, field);
        freeObjectref.putField(field, result);
        return result;
    }

    private static FreeArrayref createRepresentationForFreeArray(
            SearchingVM vm,
            ClassFile fromClass,
            String type,
            String name) {
        if (!type.startsWith("[")) {
            throw new IllegalArgumentException("Type is not an array.");
        }
        if (type.startsWith("[[")) {
            throw new UnsupportedOperationException("Nested arrays not yet regarded.");
        }
        boolean isObjectArray = type.contains("[L");
        ClassFile classFile;
        ReferenceValue referenceValue;
        // For primitive arrays
        InitializedClass initializedClass = null;
        if (isObjectArray) {
            classFile = resolveOrThrowException(vm, fromClass, type.replaceAll("\\[", ""));
            referenceValue = vm.getAFreeObjectref(classFile);
        } else {
            try {
                classFile = vm.getClassLoader()
                        .getClassAsClassFile(
                                VmSymbols.basicType2JavaClassName(VmSymbols.signature2BasicType(type.replace("[", ""))));
                referenceValue = classFile.getAPrimitiveWrapperObjectref(vm);
                initializedClass = classFile.getTheInitializedClass(vm);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

        }

        FreeArrayref result = new FreeArrayref(
                name,
                referenceValue,
                new NumericVariable(name+".length", Expression.Type.INT.toByte()),
                type.replaceFirst("\\[", ""));
        if (!isObjectArray) {
            result.setRepresentedTypeAsAPrimitiveWrapper(initializedClass);
        }

        result = createNestingIfNeeded(result, type);
        vm.getSolverManager().addConstraint(GreaterOrEqual.newInstance(result.getLengthTerm(), IntConstant.getInstance(0)));
        return result;
    }

    private static FreeArrayref createNestingIfNeeded(FreeArrayref innerArrayref, String type) {
//        int nestingCount = 0;
//        for (int i = 0; i < type.length(); i++) {
//            if (type.charAt(i) == '[') {
//                nestingCount++;
//            } else {
//                break;
//            }
//        }
        // TODO Nested arrayrefs
        return innerArrayref;
    }

    private static ClassFile resolveOrThrowException(SearchingVM vm, ClassFile fromClass, String type) throws IllegalStateException {
        // Resolve class of target type.
        try {
            return vm.resolveClassAsClassFile(fromClass, type);
        } catch (VmRuntimeException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
