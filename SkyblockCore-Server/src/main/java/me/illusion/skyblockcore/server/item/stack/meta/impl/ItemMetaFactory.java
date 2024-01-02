package me.illusion.skyblockcore.server.item.stack.meta.impl;

import java.util.Map;
import me.illusion.skyblockcore.server.item.stack.meta.ItemMeta;
import me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta;
import me.illusion.skyblockcore.server.item.stack.meta.impl.data.ItemMetaDataContainer;
import me.illusion.skyblockcore.server.item.stack.meta.impl.data.ItemMetaViewProvider;

public final class ItemMetaFactory {

    // Janky way, might redo this later
    private static final Map<Class<? extends ItemMeta>, ItemMetaViewProvider> PROVIDERS = Map.of(
        ItemMeta.class, ItemMetaView::new,
        LeatherArmorMeta.class, LeatherArmorMetaView::new
    );

    private ItemMetaFactory() {

    }

    public static <T extends ItemMeta> T create(Class<T> metaClass) {
        ItemMetaDataContainer container = ItemMetaDataContainer.empty();
        ItemMetaViewProvider provider = PROVIDERS.get(metaClass);

        if (provider == null) {
            throw new IllegalArgumentException("No provider for " + metaClass.getName());
        }

        return (T) provider.apply(container);
    }

    public static <T extends ItemMeta> T create(Class<T> metaClass, ItemMetaDataContainer container) {
        ItemMetaViewProvider provider = PROVIDERS.get(metaClass);

        if (provider == null) {
            throw new IllegalArgumentException("No provider for " + metaClass.getName());
        }

        return (T) provider.apply(container);
    }
}
