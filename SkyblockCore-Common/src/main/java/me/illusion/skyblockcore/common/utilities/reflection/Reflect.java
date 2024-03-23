package me.illusion.skyblockcore.common.utilities.reflection;

public final class Reflect {

    private Reflect() {

    }

    public static <T> ReflectedClass<T> aClass(Class<T> clazz) {
        return new ReflectedClass<>(clazz);
    }

    public static <T> ReflectedField<T> aField(Class<T> clazz, String fieldName) {
        return new ReflectedField<>(clazz, fieldName);
    }

    public static <T> ReflectedMethod aMethod(Class<T> clazz, String methodName) {
        return Reflect.aClass(clazz).getMethod(methodName);
    }

    public static <T> ReflectedConstructor<T> aConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        return new ReflectedConstructor<>(clazz, parameterTypes);
    }

    public static <T> ReflectedConstructor<T> aConstructor(Class<T> clazz) {
        return Reflect.aConstructor(clazz, new Class<?>[0]);
    }

    public static ReflectedConstructorCache aConstructorCache() {
        return new ReflectedConstructorCache();
    }

    public static UnsafeReflectedField unsafeField(Class<?> clazz, String fieldName) {
        return new UnsafeReflectedField(new ReflectedField<>(clazz, fieldName).getField());
    }

    public static UnsafeReflectedField unsafeField(String className, String fieldName) {
        try {
            return new UnsafeReflectedField(Class.forName(className).getDeclaredField(fieldName));
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}