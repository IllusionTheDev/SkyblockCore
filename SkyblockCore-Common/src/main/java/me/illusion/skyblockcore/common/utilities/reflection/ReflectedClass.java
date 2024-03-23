package me.illusion.skyblockcore.common.utilities.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectedClass<T> {

    private final Class<T> internalClass;

    private final Map<String, ReflectedField<T>> fields;
    private final Map<String, ReflectedMethod> methods;
    private final Map<String, ReflectedConstructor<?>> constructors;

    public ReflectedClass(Class<T> internalClass) {
        this.internalClass = internalClass;
        this.fields = createFields();
        this.methods = createMethods();
        this.constructors = createConstructors();
    }

    public ReflectedField<T> getField(String fieldName) {
        return fields.get(fieldName);
    }

    public <V> V getField(T instance, String fieldName) {
        return fields.get(fieldName).getValue(instance);
    }

    public void setField(T instance, String fieldName, Object value) {
        fields.get(fieldName).setValue(instance, value);
    }

    public ReflectedMethod getMethod(String methodName) {
        return methods.get(methodName);
    }

    public <V> V invoke(T instance, String methodName, Object... args) {
        return methods.get(methodName).invoke(instance, args);
    }

    public Class<T> getInternalClass() {
        return internalClass;
    }

    private Map<String, ReflectedField<T>> createFields() {
        Map<String, ReflectedField<T>> fields = new ConcurrentHashMap<>();
        parseFields(internalClass, fields);
        return fields;
    }

    private void parseFields(Class<?> clazz, Map<String, ReflectedField<T>> fields) {
        for (Field field : clazz.getDeclaredFields()) {
            fields.put(field.getName(), new ReflectedField<>(internalClass, field.getName()));
        }

        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            parseFields(clazz.getSuperclass(), fields);
        }
    }

    private Map<String, ReflectedMethod> createMethods() {
        Map<String, ReflectedMethod> methods = new ConcurrentHashMap<>();
        parseMethods(internalClass, methods);
        return methods;
    }

    private void parseMethods(Class<?> clazz, Map<String, ReflectedMethod> methods) {
        for (Method method : clazz.getDeclaredMethods()) {
            methods.put(method.getName(), new ReflectedMethod(internalClass, method.getName()));
        }

        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            parseMethods(clazz.getSuperclass(), methods);
        }
    }

    private Map<String, ReflectedConstructor<?>> createConstructors() {
        Map<String, ReflectedConstructor<?>> constructors = new ConcurrentHashMap<>();
        parseConstructors(internalClass, constructors);
        return constructors;
    }

    private void parseConstructors(Class<?> clazz, Map<String, ReflectedConstructor<?>> constructors) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            constructors.put(constructor.getName(), new ReflectedConstructor<>(clazz, constructor));
        }

        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            parseConstructors(clazz.getSuperclass(), constructors);
        }
    }

}