package me.illusion.skyblockcore.spigot.pasting;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.pasting.handler.DefaultHandler;
import me.illusion.skyblockcore.spigot.pasting.handler.FAWEHandler;
import org.bukkit.Bukkit;

public enum PastingType {
    DEFAULT(),
    FAWE("FastAsyncWorldEdit");

    private static final PastingType[] VALUES = {
            DEFAULT,
            FAWE,
    };

    private String plugin = null;

    PastingType() {
    }

    PastingType(String plugin) {
        this.plugin = plugin;
    }

    public static PastingHandler enable(SkyblockPlugin main, String selection) {
        PastingType type = valueOf(selection);

        if (!type.canEnable())
            return DEFAULT.initialize(main);

        return type.initialize(main);
    }

    public static PastingType[] getValues() {
        return VALUES;
    }

    public boolean canEnable() {
        return plugin == null || Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    private PastingHandler initialize(SkyblockPlugin main) {
        if (this == DEFAULT)
            return new DefaultHandler();
        else
            return new FAWEHandler(main);
    }

    public String getPlugin() {
        return plugin;
    }
}
