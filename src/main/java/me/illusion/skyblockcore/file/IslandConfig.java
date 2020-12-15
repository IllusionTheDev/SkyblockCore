package me.illusion.skyblockcore.file;

import lombok.Getter;
import me.illusion.skyblockcore.file.path.ExtendedYMLBase;
import me.illusion.skyblockcore.file.settings.WorldSettings;
import me.illusion.utilities.storage.YMLBase;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class IslandConfig extends ExtendedYMLBase {

    private final WorldSettings overworldSettings;
    private final WorldSettings netherSettings;

    public IslandConfig(JavaPlugin plugin) {
        super(plugin, "island-config.yml");

        overworldSettings = load(new WorldSettings(), getConfiguration().getConfigurationSection("island.overworld"));
        netherSettings = load(new WorldSettings(), getConfiguration().getConfigurationSection("island.nether"));
    }
}
