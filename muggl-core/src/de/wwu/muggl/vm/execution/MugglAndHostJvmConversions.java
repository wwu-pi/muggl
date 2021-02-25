package de.wwu.muggl.vm.execution;

import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.FieldResolutionError;
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.execution.nativeWrapping.PrintStreamWrapper;
import de.wwu.muggl.vm.initialization.*;
import de.wwu.muggl.vm.initialization.strings.StringCache;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

// TODO Some problems remain: It might happen that a class references a static other class and the static other class
//  has not been translated.
public class MugglAndHostJvmConversions {
    public MugglAndHostJvmConversions() {
    }
    protected final Map<Class<?>, Constructor<?>> forcedEmptyConstructors = new HashMap<>();
    protected final ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
    protected final Map<Object, Object> mugglToHostJvmMapping = new HashMap<>();

    protected Constructor<?> getHostJvmConstructorForInitializedClass(InitializedClass ic) {
        return getForcedEmptyConstructor(ic.getClassFile().getInstanceOfClass());
    }

    protected Object getNewHostJvmInstanceForInitializedClass(InitializedClass ic) {
        return newInstance(ic.getClassFile().getInstanceOfClass());
    }

    protected Constructor<?> getForcedEmptyConstructor(Class<?> c) {
        Constructor<?> result = forcedEmptyConstructors.get(c);
        if (result != null) {
            return result;
        }
        try {
            result = c.getConstructor(new Class[0]);
            forcedEmptyConstructors.put(c, result);
            return result;
        } catch (NoSuchMethodException e) {/* Ignore, we will try to create a new empty constructor: */}
        Class<?> superClass = c.getSuperclass();
        if (superClass != null) {
            Constructor<?> superClassForcedConstructor = getForcedEmptyConstructor(superClass);
            result = reflectionFactory.newConstructorForSerialization(c, superClassForcedConstructor);
        } else {
            // Only object has no super class.
            if (c != Object.class) {
                throw new IllegalStateException("Only the Object class should have no super class.");
            }
            try {
                result = c.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Object class should have empty constructor.", e);
            }
        }

        if (result == null) {
            throw new IllegalStateException("Empty constructor should have been found.");
        }

        forcedEmptyConstructors.put(c, result);
        return result;
    }

    public Object newInstance(Class<?> c) {
        if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) {
            throw new IllegalArgumentException("Cannot instantiate interface or abstract class.");
        }
        Constructor<?> constr = getForcedEmptyConstructor(c);
        try {
            return constr.newInstance();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstanceOfType(Class<T> c) {
        return (T) newInstance(c);
    }

    /* TRANSFORM MUGGL REPRESENTATION TO HOST JVM OBJECTS */

    public Object toObjectOfHostJvm(Object o) {
        return toObjectOfHostJvm(o, mugglToHostJvmMapping);
    }

    public Object toObjectOfHostJvm(Object o, Map<Object, Object> mugglToHostJvmMapping) {
        if (o == null) {
            return null;
        }
        Object result = mugglToHostJvmMapping.get(o);
        if (result != null) {
            return result;
        }
        if (o instanceof IntConstant) {
            return ((IntConstant) o).getIntValue();
        } else if (o instanceof DoubleConstant) {
            return ((DoubleConstant) o).getValue();
        } else if (o instanceof FloatConstant) {
            return ((FloatConstant) o).getValue();
        } else if (o instanceof NumericVariable) {
            throw new IllegalStateException("NumericVariables have to be labeled before translating them into a host JVM's representation.");
        } else if (o instanceof Objectref) {
            if (o instanceof FreeObjectref && !((FreeObjectref) o).isOfASpecificType()) {
                throw new IllegalStateException("FreeObjectrefs should be concretized before copying them onto the host JVM.");
            }
            return transformObjectrefToObject((Objectref) o, mugglToHostJvmMapping);
        } else if (o instanceof Arrayref) {
            if (o instanceof FreeArrayref && !((FreeArrayref) o).isConcretized()) {
                throw new IllegalStateException("FreeArrayrefs should be concretized before copying them onto the host JVM.");
            }
            return transformArrayrefToArray((Arrayref) o, mugglToHostJvmMapping);
        } else if (o instanceof String || o instanceof Double || o instanceof Float || o instanceof Long
                || o instanceof Integer || o instanceof Short || o instanceof Byte || o instanceof Boolean
                || o instanceof Character) {
            return o;
        } else {
            throw new IllegalArgumentException("Unexpected input argument: " + o);
        }
    }

    protected Object transformArrayrefToArray(Arrayref ar, Map<Object, Object> mugglToHostJvmMapping) {
        ClassFile classFile = ar.getInitializedClass().getClassFile();
        Class<?> classOfElements = classFile.getInstanceOfClass();
        if (isPrimitive(ar)) {
            String className = classFile.getName();
            if (className.equals(Double.class.getName())) {
                classOfElements = double.class;
            } else if (className.equals(Float.class.getName())) {
                classOfElements = float.class;
            } else if (className.equals(Long.class.getName())) {
                classOfElements = long.class;
            } else if (className.equals(Integer.class.getName())) {
                classOfElements = int.class;
            } else if (className.equals(Short.class.getName())) {
                classOfElements = short.class;
            } else if (className.equals(Byte.class.getName())) {
                classOfElements = byte.class;
            } else if (className.equals(Boolean.class.getName())) {
                classOfElements = boolean.class;
            } else if (className.equals(Character.class.getName())) {
                classOfElements = char.class;
            } else {
                throw new IllegalStateException("Unknown className: " + className);
            }
        }
        Object result;
        if (ar instanceof FreeArrayref) {
            result = Array.newInstance(classOfElements, ar.getLength());
        } else {
            result = Array.newInstance(classOfElements, ar.getDimensions());
        }
        mugglToHostJvmMapping.put(ar, result);
        insertArrayrefIntoArray(result, ar);
        return result;
    }

    protected boolean isPrimitive(Arrayref ar) {
        if (ar.isPrimitive()) {
            return true;
        }
        if (ar.getLength() > 0 && ar.getElement(0) instanceof Arrayref) {
            return isPrimitive((Arrayref) ar.getElement(0));
        }
        return false;
    }

    // Copied and adapted from MugglToJavaConversion
    private void insertArrayrefIntoArray(Object array, Arrayref arrayref) {
        for (int a = 0; a < arrayref.getLength(); a++) {
            // Check if the elements are arrays, too.
            if (arrayref.getLength() > 0 && arrayref.getElement(0) != null
                    && (arrayref.getReferenceValue().isArray() || arrayref.getElement(0) instanceof Arrayref)) {
                // Recursively invoke this method.
                insertArrayrefIntoArray(Array.get(array, a), (Arrayref) arrayref.getElement(a));
            } else {
                // Insert the value after converting it to java.
                try {
                    Array.set(array, a, toObjectOfHostJvm(arrayref.getElement(a), mugglToHostJvmMapping));
                } catch (Exception e) {
                    System.out.println(e);
                    throw e;
                }
            }
        }
    }


    protected Object transformObjectrefToObject(Objectref or, Map<Object, Object> mugglToHostJvmMapping) {
        InitializedClass mugglInitializedClass = or.getInitializedClass();
        Class<?> currentClass = mugglInitializedClass.getClassFile().getInstanceOfClass();
        if (Class.class == currentClass) {
            return or.getInitializedClass().getClassFile().getInstanceOfClass();
        } else if (PrintStreamWrapper.class == currentClass) {
            String wrapperName = (String) or.getField(or.getFieldForName("wrapperFor"));
            if ("java.lang.System.out".equals(wrapperName)) {
                return System.out;
            }
        } else if (Thread.class.isAssignableFrom(currentClass)) {
            throw new IllegalStateException("Should probably be specially regarded. Not yet implemented: " + currentClass);
        }
        if (currentClass == String.class) {
            Arrayref charValues = (Arrayref) or.getField(or.getFieldForName("value"));
            char[] chars = new char[charValues.getLength()];
            for (int i = 0; i < chars.length; i++) {
                // TODO Seemingly this is sometimes represented by Characters and sometimes by Integers?
                Object element = charValues.getElement(i);
                if (charValues.getElement(i) instanceof Character) {
                    chars[i] = (Character) element;
                } else {
                    chars[i] = (char) ((IntConstant) element).getIntValue();
                }
            }
            String result = new String(chars);
            mugglToHostJvmMapping.put(or, result);
            return result;
        }
        Object result = getNewHostJvmInstanceForInitializedClass(mugglInitializedClass);
        mugglToHostJvmMapping.put(or, result);
        copyMugglFieldsToHostJvmObject(or, result, mugglToHostJvmMapping);
        return result;
    }

    protected void copyMugglFieldsToHostJvmObject(Objectref or, Object o, Map<Object, Object> mugglToHostJvmMapping) {
        try {
            Map<Field, Object> fields = or.getFields();
            Class<?> resultClass = o.getClass();
            Set<java.lang.reflect.Field> hostVmFields = getHostVmFields(resultClass);
            for (Map.Entry<Field, Object> entry : fields.entrySet()) {
                java.lang.reflect.Field resultField = getHostVmFieldForMugglField(entry.getKey(), hostVmFields);
                Object resultFieldValue = toObjectOfHostJvm(entry.getValue(), mugglToHostJvmMapping);
                if ((resultField.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == (Modifier.FINAL | Modifier.STATIC)) {
                    continue;
                }
                boolean isAccessible = resultField.isAccessible();
                resultField.setAccessible(true);
                // Muggl often uses Integers to represent booleans
                if (resultField.getType() == Boolean.class || resultField.getType() == boolean.class) {
                    if (resultFieldValue instanceof Integer) {
                        resultFieldValue = (((Integer) resultFieldValue) == 1);
                    } else if (resultFieldValue instanceof IntConstant) {
                        resultFieldValue = ((IntConstant) resultFieldValue).getIntValue() == 1;
                    }
                }
                resultField.set(o, resultFieldValue);
                resultField.setAccessible(isAccessible);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    protected java.lang.reflect.Field getHostVmFieldForMugglField(Field f, Set<java.lang.reflect.Field> hostVmFields) {
        for (java.lang.reflect.Field hostVmField : hostVmFields) {
            if (hostVmField.getName().equals(f.getName())) {
                return hostVmField;
            }
        }
        throw new IllegalStateException("Field not found: " + f);
    }

    protected Set<java.lang.reflect.Field> getHostVmFields(Class<?> getFieldFor) {
        Set<java.lang.reflect.Field> result = new HashSet<>();
        while (getFieldFor != null) {
            result.addAll(Arrays.asList(getFieldFor.getDeclaredFields()));
            getFieldFor = getFieldFor.getSuperclass();
        }
        return result;
    }


    /* TRANSFORM HOST JVM OBJECTS TO MUGGL REPRESENTATION */
    protected final MugglClassLoader mugglClassLoader = VirtualMachine.getLatestVM().getClassLoader();
    protected final VirtualMachine vm = VirtualMachine.getLatestVM();
    protected final Map<Object, Object> hostJvmToMugglMapping = new HashMap<>();

    protected Objectref newObjectrefForClass(Class<?> hostJvmClass, boolean primitiveWrapper) {
        try {
            hostJvmClass = toPrimitiveWrapper(hostJvmClass);
            ClassFile classFile = mugglClassLoader.getClassAsClassFile(hostJvmClass);
            return primitiveWrapper ? classFile.getAPrimitiveWrapperObjectref(vm) : vm.getAnObjectref(classFile);
        } catch (ClassFileException | PrimitiveWrappingImpossibleException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Class<?> toPrimitiveWrapper(Class<?> c) {
        if (c == double.class) {
            return Double.class;
        } else if (c == float.class) {
            return Float.class;
        } else if (c == long.class) {
            return Long.class;
        } else if (c == int.class) {
            return Integer.class;
        } else if (c == short.class) {
            return Short.class;
        } else if (c == byte.class) {
            return Byte.class;
        } else if (c == char.class) {
            return Character.class;
        } else if (c == boolean.class) {
            return Boolean.class;
        } else {
            return c;
        }
    }

    protected Objectref newObjectrefForClass(Class<?> hostJvmClass) {
        return newObjectrefForClass(hostJvmClass, false);
    }

    public Object toObjectInMugglJvm(Object o, boolean isPrimitive) {
        return toObjectInMugglJvm(o, isPrimitive, hostJvmToMugglMapping);
    }

    public Object toObjectInMugglJvm(Object o, boolean isPrimitive, Map<Object, Object> hostJvmToMugglMapping) {
        if (o == null) {
            return null;
        }
        if (o instanceof Objectref
                || o instanceof Arrayref
                || o instanceof Term
                || o instanceof BooleanConstant) {
            // This mix is expected for transforming a Solution-object in SolutionIterator.
            return o;
        }
        Object result = hostJvmToMugglMapping.get(o);
        if (result != null && !isPrimitive) {
            // The check for isPrimitive is needed. Otherwise we could not distinguish between cached
            // wrapper variables and cached primitives.
            return result;
        }
        if (isPrimitive) {
            // Must be Double, Float, Long, Integer, Short, Byte, Boolean, or Character. Primitives are
            // represented as these types in Muggl.
            return o;
        }
//        if (o.getClass() == Class.class) { /// TODO
//            try {
//                return mugglClassLoader.getClassAsClassFile(o.getClass()).getInitializedClass();
//            } catch (Exception e) { throw new IllegalStateException(e); }
//        }
        Class<?> hostClass = o.getClass();

        if (hostClass.isArray()) {
            return transformArrayToArrayref(o, hostJvmToMugglMapping);
        } else {
            // Normal object. Possibly wrapper object. Regardless, an Objectref will be initialized.
            return transformObjectToObjectref(o, hostJvmToMugglMapping);
        }
    }

    protected Object transformArrayToArrayref(Object o, Map<Object, Object> hostJvmToMugglMapping) {
        int arrayLength = Array.getLength(o);
        Class<?> componentType = o.getClass().getComponentType();
        boolean isPrimitive = componentType.isPrimitive();
        Objectref referenceValue = newObjectrefForClass(componentType, isPrimitive);
        Arrayref result;
        if (componentType.isArray()) {
            result = new Arrayref(referenceValue, getDimensionCount(o));
        } else {
            result = new Arrayref(referenceValue, arrayLength);
        }
        hostJvmToMugglMapping.put(o, result);
        for (int i = 0; i < arrayLength; i++) {
            Object elementValue = toObjectInMugglJvm(Array.get(o, i), isPrimitive, hostJvmToMugglMapping);
            result.putElement(i, elementValue);
        }
        return result;
    }

    // Copied from MugglToJavaConversion
    private int[] getDimensionCount(Object array) {
        // Browse through the array to determine its dimensions.
        int[] dimensionCount = new int[0];
        while (true) {
            int[] dimensionCountNew = new int[dimensionCount.length + 1];
            for (int a = 0; a < dimensionCount.length; a++) {
                dimensionCountNew[a] = dimensionCount[a];
            }
            dimensionCountNew[dimensionCount.length] = Array.getLength(array);
            dimensionCount = dimensionCountNew;
            if (Array.getLength(array) > 0 && Array.get(array, 0) != null
                    && Array.get(array, 0).getClass().isArray()) {
                array = Array.get(array, 0);
            } else {
                break;
            }
        }
        return dimensionCount;
    }

    protected boolean ignoreHostJvmField(java.lang.reflect.Field f) {
        return f.getName().contains("jacoco");
    }

    protected boolean isFieldInInitializedClass(String fieldName, Set<Field> initializedStaticFields) {
        for (Field field : initializedStaticFields) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    protected Objectref transformObjectToObjectref(Object o, Map<Object, Object> hostJvmToMugglMapping) {
        Class<?> oClass = o.getClass();
        if (oClass == String.class) {
            Objectref stringObjectRef = vm.getStringCache().getStringObjectref((String) o);
            hostJvmToMugglMapping.put(o, stringObjectRef);
            return stringObjectRef;
        }
        Objectref result = newObjectrefForClass(oClass);
        hostJvmToMugglMapping.put(o, result);
        Map<java.lang.reflect.Field, Field> allMugglFields = new HashMap<>();
        InitializedClass ic = result.getInitializedClass();
        Set<Field> initializedStaticFields = ic.getStaticFields().keySet();
        ClassFile classFile = ic.getClassFile();

        while (oClass != null) {
            java.lang.reflect.Field[] declaredFields = oClass.getDeclaredFields();
            for (java.lang.reflect.Field f : declaredFields) {
                if (ignoreHostJvmField(f)) {
                    continue;
                }
                if ((f.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == (Modifier.STATIC | Modifier.FINAL)) {
                    if (isFieldInInitializedClass(f.getName(), initializedStaticFields)) {
                        continue;
                    }
                }
                allMugglFields.put(f, classFile.getFieldByName(f.getName(), true));
            }
            oClass = oClass.getSuperclass();
        }
        try {
            for (Map.Entry<java.lang.reflect.Field, Field> entry : allMugglFields.entrySet()) {
                java.lang.reflect.Field f = entry.getKey();
                boolean isAccessible = f.isAccessible();
                f.setAccessible(true);

                Object val;
                boolean isStaticField = (f.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
                if (isStaticField) {
                    val = f.get(null);
                } else {
                    val = f.get(o);
                }

                Object mugglValue = toObjectInMugglJvm(
                        val,
                        f.getType().isPrimitive(),
                        hostJvmToMugglMapping
                );
                f.setAccessible(isAccessible);
                if (isStaticField) {
                    result.getInitializedClass().putField(entry.getValue(), mugglValue);
                } else {
                    result.putField(entry.getValue(), mugglValue);
                }
            }
            return result;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
