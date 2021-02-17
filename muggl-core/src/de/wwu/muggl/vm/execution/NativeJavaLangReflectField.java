package de.wwu.muggl.vm.execution;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.PrimitiveWrappingImpossibleException;

import java.lang.invoke.MethodType;
import java.util.Map;

public class NativeJavaLangReflectField extends NativeMethodProvider {
    public static final String packageName = java.lang.reflect.Field.class.getName();

    public static Object get(Frame frame, Objectref fieldRef, Objectref getValueFrom) {
        // TODO This is just a quick fix. Accesses etc. are not checked.
        Field nameField = fieldRef.getFieldForName("name");
        Objectref nameOfFieldStringObjectref = (Objectref) fieldRef.getField(nameField);
        Field typeClassField = fieldRef.getFieldForName("type");
        Objectref typeClass = (Objectref) fieldRef.getField(typeClassField);
        Field typeField = typeClass.getFieldForName("name");
        String type = ((Objectref) typeClass.getField(typeField)).getDebugHelperString();
        boolean primitive = false;
        if (type.equals(int.class.getName())) {
            type = Integer.class.getName();
            primitive = true;
        } else if (type.equals(boolean.class.getName())) {
            type = Integer.class.getName(); // Muggl encodes Booleans as Integers (0 and 1)
            primitive = true;
        } else if (type.equals(double.class.getName())) {
            type = Double.class.getName();
            primitive = true;
        } else if (type.equals(float.class.getName())) {
            type = Float.class.getName();
            primitive = true;
        } else if (type.equals(byte.class.getName())) {
            type = Byte.class.getName();
            primitive = true;
        } else if (type.equals(short.class.getName())) {
            type = Short.class.getName();
            primitive = true;
        } else if (type.equals(long.class.getName())) {
            type = Long.class.getName();
            primitive = true;
        } else if (type.equals(char.class.getName())) {
            type = Character.class.getName();
            primitive = true;
        }

        String fieldName = nameOfFieldStringObjectref.getDebugHelperString();
        Field valueField = getValueFrom.getFieldForName(fieldName);
        Object value = getValueFrom.getField(valueField);

        Object result;
        if (primitive) {
            // Auto-boxing required.
            try {
                ClassFile cf = frame.getVm().getClassLoader().getClassAsClassFile(type);
                result = ((SearchingVM) frame.getVm()).getAPrimitiveWrapperObjectref(cf);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

            Field resultObjectField = ((Objectref) result).getFieldForName("value");
            if (type.equals(Integer.class.getName()) && value instanceof Boolean) {
                ((Objectref) result).putField(resultObjectField, ((Boolean) value) ? 1 : 0);
            } else {
                ((Objectref) result).putField(resultObjectField, value);
            }
        } else {
            result = value;
        }
        return result;
    }

    public static void registerNatives() {
        NativeWrapper.registerNativeMethod(NativeJavaLangReflectField.class, packageName, "get",
                MethodType.methodType(Object.class, Frame.class, Objectref.class, Objectref.class),
                MethodType.methodType(Object.class, Object.class));
    }
}
