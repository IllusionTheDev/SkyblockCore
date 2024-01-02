package me.illusion.skyblockcore.server.item.stack.meta.value;

import java.util.function.Supplier;

public class SimpleMetaValue<T> implements MetaValue<T> {

    private final Supplier<T> defaultValue;

    public SimpleMetaValue(T defaultValue) {
        this.defaultValue = () -> defaultValue;
    }

    public SimpleMetaValue(Supplier<T> defaultValue) {
        if (defaultValue == null) {
            defaultValue = () -> null;
        }

        this.defaultValue = defaultValue;
    }

    public static <T> SimpleMetaValue<T> create(T defaultValue) {
        return new SimpleMetaValue<>(defaultValue);
    }

    public static <T> SimpleMetaValue<T> create(Supplier<T> defaultValue) {
        return new SimpleMetaValue<>(defaultValue);
    }

    @Override
    public T getDefaultValue() {
        return defaultValue.get();
    }
}
