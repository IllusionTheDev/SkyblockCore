package me.illusion.skyblockcore.spigot.file;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.utilities.storage.YMLBase;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SetupData extends YMLBase {

    private final ServerType serverType;

    public SetupData(JavaPlugin plugin) {
        super(plugin, "setup-settings.yml");

        serverType = ServerType.valueOf(getConfiguration().getString("server-type").toUpperCase());
    }

    public enum ServerType {
        ISLAND,
        GENERIC
    }
}
