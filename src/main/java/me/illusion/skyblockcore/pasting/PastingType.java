package me.illusion.skyblockcore.pasting;

import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.pasting.handler.DefaultHandler;
import me.illusion.skyblockcore.pasting.handler.FAWEHandler;
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

    public static PastingHandler enable(CorePlugin main, String selection) {
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

    private PastingHandler initialize(CorePlugin main) {
        if (this == DEFAULT)
            return new DefaultHandler();
        else
            return new FAWEHandler(main);
    }

    public String getPlugin() {
        return plugin;
    }
}
