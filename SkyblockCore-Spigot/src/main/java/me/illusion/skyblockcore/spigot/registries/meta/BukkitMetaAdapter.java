package me.illusion.skyblockcore.spigot.registries.meta;

import java.util.List;
import me.illusion.skyblockcore.server.item.stack.meta.ItemMeta;
import me.illusion.skyblockcore.server.item.stack.meta.impl.ItemMetaFactory;
import me.illusion.skyblockcore.spigot.registries.meta.converter.DefaultItemMetaAdapter;
import me.illusion.skyblockcore.spigot.registries.meta.converter.LeatherItemMetaAdapter;
import me.illusion.skyblockcore.spigot.registries.meta.converter.MetaAdapter;

public final class BukkitMetaAdapter {

    private static final List<MetaAdapter<?, ?>> META_ADAPTERS = List.of(
        new DefaultItemMetaAdapter(),
        new LeatherItemMetaAdapter()
    );

    private BukkitMetaAdapter() {

    }

    public static <B, P extends ItemMeta> ItemMeta adapt(org.bukkit.inventory.meta.ItemMeta bukkitMeta) {
        ItemMeta platformMeta = ItemMetaFactory.create(ItemMeta.class);

        for (MetaAdapter<?, ?> adapter : META_ADAPTERS) {
            MetaAdapter<B, P> metaAdapter = (MetaAdapter<B, P>) adapter;

            Class<B> bukkitMetaClass = metaAdapter.getBukkitMetaClass();
            Class<P> platformMetaClass = metaAdapter.getPlatformMetaClass();

            if (!bukkitMetaClass.isAssignableFrom(bukkitMeta.getClass())) {
                continue;
            }

            P platformMetaInstance = platformMeta.as(platformMetaClass);
            metaAdapter.convertToPlatform(bukkitMetaClass.cast(bukkitMeta), platformMetaInstance);
        }

        return platformMeta;
    }

}
