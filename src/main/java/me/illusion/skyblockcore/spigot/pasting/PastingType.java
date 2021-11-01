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
        PastingType type = matchSelection(selection);

        if (type == null || !type.canEnable())
            return DEFAULT.initialize(main);

        return type.initialize(main);
    }

    public static PastingType[] getValues() {
        return VALUES;
    }

    public boolean canEnable() {
        return plugin == null || Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    private static PastingType matchSelection(String selection) {
        for (PastingType type : VALUES)
            if (type.name().equalsIgnoreCase(selection))
                return type;
        return null;
    }

    private PastingHandler initialize(SkyblockPlugin main) {
        if (this == DEFAULT)
            return new DefaultHandler(main);
        else
            return new FAWEHandler(main);
    }

    public String getPlugin() {
        return plugin;
    }
}
