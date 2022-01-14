package me.illusion.skyblockcore.spigot.file;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.utilities.storage.YMLBase;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SettingsFile extends YMLBase {

    private final int saveDelay;
    private final int releaseDelay;

    public SettingsFile(JavaPlugin plugin) {
        super(plugin, "settings.yml");

        saveDelay = getConfiguration().getInt("delay.after-save");
        releaseDelay = getConfiguration().getInt("delay.release-world");
    }
}
