package me.illusion.skyblockcore.spigot.registries.meta;

import java.util.List;
import me.illusion.skyblockcore.spigot.registries.meta.converter.LeatherMetaConverter;
import me.illusion.skyblockcore.spigot.registries.meta.converter.MetaConverter;
import me.illusion.skyblockcore.spigot.registries.meta.converter.SimpleMetaConverter;
import org.bukkit.inventory.meta.ItemMeta;

public final class BukkitMetaAdapter {

    private static final List<MetaConverter<?, ?>> ADAPTERS = List.of(
        new LeatherMetaConverter(),
        new SimpleMetaConverter()
    );

    private BukkitMetaAdapter() {

    }

    public static <K extends ItemMeta, V extends me.illusion.skyblockcore.server.item.stack.ItemMeta> me.illusion.skyblockcore.server.item.stack.ItemMeta adapt(
        ItemMeta bukkitMeta) {
        for (MetaConverter<?, ?> adapter : ADAPTERS) {
            MetaConverter<K, V> metaConverter = (MetaConverter<K, V>) adapter;
            Class<K> metaClass = metaConverter.getMetaClass();

            if (metaClass.isAssignableFrom(bukkitMeta.getClass())) {
                return metaConverter.convert(metaClass.cast(bukkitMeta));
            }
        }

        throw new IllegalArgumentException("No adapter found for " + bukkitMeta.getClass().getName());
    }


}
