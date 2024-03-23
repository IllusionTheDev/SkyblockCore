package me.illusion.skyblockcore.common.utilities.reflection;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeReflectedField {

    private final long offset;

    public UnsafeReflectedField(Field field) {
        this.offset = getUnsafe().objectFieldOffset(field);
    }

    public void set(Object object, Object value) {
        getUnsafe().putObject(object, offset, value);
    }

    private Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
