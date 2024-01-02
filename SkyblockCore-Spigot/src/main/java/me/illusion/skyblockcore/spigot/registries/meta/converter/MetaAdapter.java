package me.illusion.skyblockcore.spigot.registries.meta.converter;

public interface MetaAdapter<B, P> {

    Class<B> getBukkitMetaClass();

    Class<P> getPlatformMetaClass();

    void convertToPlatform(B bukkitMeta, P platformMeta);

    void convertToBukkit(P platformMeta, B bukkitMeta);

}
