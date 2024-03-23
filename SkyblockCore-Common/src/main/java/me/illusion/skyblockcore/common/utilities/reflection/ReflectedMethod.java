package me.illusion.skyblockcore.common.utilities.reflection;

import java.lang.reflect.Method;

public class ReflectedMethod {

    private final Class<?> internalClass;
    private final String methodName;
    private final Class<?>[] parameterTypes;

    private final Method method;

    public ReflectedMethod(Class<?> internalClass, String methodName) {
        this.internalClass = internalClass;
        this.methodName = methodName;

        try {
            this.method = internalClass.getDeclaredMethod(methodName);
            this.parameterTypes = method.getParameterTypes();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V invoke(Object instance, Object... args) {
        boolean accessible = method.isAccessible();

        try {
            method.setAccessible(true);
            return (V) method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            method.setAccessible(accessible);
        }
    }

    public Class<?> getInternalClass() {
        return internalClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Method getMethod() {
        return method;
    }
}