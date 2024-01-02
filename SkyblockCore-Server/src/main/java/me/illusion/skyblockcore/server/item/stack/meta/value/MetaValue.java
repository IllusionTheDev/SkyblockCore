package me.illusion.skyblockcore.server.item.stack.meta.value;

import java.util.function.Supplier;

public interface MetaValue<T> {

    static <T> MetaValue<T> create(T defaultValue) {
        return SimpleMetaValue.create(defaultValue);
    }

    static <T> MetaValue<T> create(Supplier<T> defaultValue) {
        return SimpleMetaValue.create(defaultValue);
    }

    static <T> MetaValue<T> create() {
        return SimpleMetaValue.create(null);
    }

    T getDefaultValue();

}
