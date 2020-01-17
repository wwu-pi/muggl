package de.wwu.muggl.vm.initialization;

import de.wwu.muggl.solvers.expressions.BooleanVariable;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;

public class FreeObjectrefInitialisers {
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
            throw new IllegalStateException("Free variables of array types are not supported yet.");
        } else if (expressionType == Expression.Type.BOOLEAN) {
            freeVariableRepresentation = new BooleanVariable(name);
        } else {
            freeVariableRepresentation = new NumericVariable(name, expressionType.toByte(), false);
        }
        return freeVariableRepresentation;
    }

    private static Object createRepresentationForFreeObject(SearchingVM vm, ClassFile fromClass, String type) {
	    // Resolve class of target type.
        ClassFile classFile;
        try {
            classFile = vm.resolveClassAsClassFile(fromClass, type);
        } catch (VmRuntimeException | ExecutionException e) {
            throw new IllegalStateException(e);
        }

        // Get an uninitialised(!) Objectref.
        FreeObjectref anObjectref = vm.getAFreeObjectref(classFile);

        // Do not call initialisers:
        // - the static one already ran (via getAnObjectref).
        // - the instance one is not called on purpose, see paper.

        for (Field field : classFile.getFields()) {
            if (anObjectref.hasValueFor(field)) {
                continue;
            }
            // Initialise uninitialised fields as free.
            String fieldType = field.getDescriptor();
            Object representation = createRepresentationForFreeVariableOrField(vm, classFile, fieldType, field.getName());
            anObjectref.putField(field, representation);
        }
        return anObjectref;
    }
}
