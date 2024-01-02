package me.illusion.skyblockcore.common.registry;

import java.util.Locale;
import java.util.regex.Pattern;

public class SkyblockNamespacedKey {

    private static final Pattern PATTERN = Pattern.compile("^[a-z0-9._-]+$");
    private static final String MINECRAFT_NAMESPACE = "minecraft";
    private static final String SKYBLOCK_NAMESPACE = "skyblock";

    private final String namespace;
    private final String key;

    public SkyblockNamespacedKey(String namespace, String key) {
        this.namespace = namespace.toLowerCase(Locale.ROOT);
        this.key = key.toLowerCase(Locale.ROOT);

        validatePattern();
    }

    public static SkyblockNamespacedKey minecraft(String key) {
        return new SkyblockNamespacedKey(MINECRAFT_NAMESPACE, key);
    }

    public static SkyblockNamespacedKey skyblock(String key) {
        return new SkyblockNamespacedKey(SKYBLOCK_NAMESPACE, key);
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

    private void validatePattern() {
        if (!PATTERN.matcher(namespace).matches()) {
            throw new IllegalArgumentException("Namespace '" + namespace + "' does not match pattern " + PATTERN.pattern());
        }

        if (!PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("Key '" + key + "' does not match pattern " + PATTERN.pattern());
        }
    }

    public SkyblockNamespacedKey fromInput(String input) {
        String[] split = input.split(":");

        String namespace;
        String key;

        if (split.length == 1) {
            namespace = MINECRAFT_NAMESPACE;
            key = split[0];
        } else if (split.length == 2) {
            namespace = split[0];
            key = split[1];
        } else {
            throw new IllegalArgumentException("Input '" + input + "' does not follow the pattern namespace:key");
        }

        return new SkyblockNamespacedKey(namespace, key);
    }
}
