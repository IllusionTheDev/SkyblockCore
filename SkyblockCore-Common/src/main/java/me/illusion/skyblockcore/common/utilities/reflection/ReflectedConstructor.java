package me.illusion.skyblockcore.common.utilities.reflection;

import java.lang.reflect.Constructor;

public class ReflectedConstructor<T> {

    private final Class<?> internalClass;
    private final Class<?>[] parameterTypes;
    private final Constructor<T> constructor;

    public ReflectedConstructor(Class<T> internalClass, Class<?>... parameterTypes) {
        this.internalClass = internalClass;
        this.parameterTypes = parameterTypes;

        try {
            this.constructor = internalClass.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public ReflectedConstructor(Class<?> internalClass, Constructor<T> javaConstructor) {
        this.internalClass = internalClass;
        this.parameterTypes = javaConstructor.getParameterTypes();
        this.constructor = javaConstructor;
    }

    public T newInstance(Object... args) {
        boolean accessible = constructor.isAccessible();

        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            constructor.setAccessible(accessible);
        }
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

}
