package me.illusion.skyblockcore.common.registry;

public class SkyblockNamespacedKey {

    private final String namespace;
    private final String key;

    public SkyblockNamespacedKey(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    public static SkyblockNamespacedKey minecraft(String key) {
        return new SkyblockNamespacedKey("minecraft", key);
    }

    public static SkyblockNamespacedKey skyblock(String key) {
        return new SkyblockNamespacedKey("skyblock", key);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }
}
