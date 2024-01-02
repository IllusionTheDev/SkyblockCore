package me.illusion.skyblockcore.server.item.stack.meta.impl.data;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import me.illusion.skyblockcore.server.item.stack.meta.value.MetaValue;

public class ItemMetaDataContainer {

    private final Map<MetaValue<?>, Object> values;

    private ItemMetaDataContainer(Map<MetaValue<?>, Object> values) {
        this.values = Map.copyOf(values);
    }

    public static ItemMetaDataContainer empty() {
        return new ItemMetaDataContainer(Collections.emptyMap());
    }

    public static ItemMetaDataContainer of(Map<MetaValue<?>, Object> values) {
        return new ItemMetaDataContainer(values);
    }

    public <T> T get(MetaValue<T> metaValue) {
        return (T) values.getOrDefault(metaValue, metaValue.getDefaultValue());
    }

    public <T> ItemMetaDataContainer set(MetaValue<T> metaValue, T value) {
        return modify(map -> map.put(metaValue, value));
    }

    public <T> ItemMetaDataContainer remove(MetaValue<T> metaValue) {
        return modify(map -> map.remove(metaValue));
    }

    private ItemMetaDataContainer modify(Consumer<Map<MetaValue<?>, Object>> consumer) {
        Map<MetaValue<?>, Object> newValues = new ConcurrentHashMap<>(values);
        consumer.accept(newValues);
        return new ItemMetaDataContainer(newValues);
    }

    public Map<MetaValue<?>, Object> getValues() {
        return values;
    }
}
