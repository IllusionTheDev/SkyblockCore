package me.illusion.skyblockcore.common.utilities.reflection;

import java.lang.reflect.Field;

public class ReflectedField<T> {

    private final Class<T> internalClass;
    private final String fieldName;
    private final Class<?> fieldType;

    private final Field field;

    public ReflectedField(Class<T> internalClass, String fieldName) {
        this.internalClass = internalClass;
        this.fieldName = fieldName;

        try {
            this.field = internalClass.getDeclaredField(fieldName);
            this.fieldType = field.getType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V getValue(Object instance) {
        try {
            return (V) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(T instance, Object value) {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(accessible);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<T> getInternalClass() {
        return internalClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public Field getField() {
        return field;
    }
}