package me.illusion.skyblockcore.spigot.file;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.file.path.ExtendedYMLBase;
import me.illusion.skyblockcore.spigot.file.settings.WorldSettings;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class IslandConfig extends ExtendedYMLBase {

    private final int worldCount;
    private final String pastingSelection;
    private final WorldSettings overworldSettings;
    private final WorldSettings netherSettings;

    public IslandConfig(JavaPlugin plugin) {
        super(plugin, "island-config.yml");

        pastingSelection = getConfiguration().getString("island.pasting-type");
        worldCount = getConfiguration().getInt("island.island-worlds", 25);
        overworldSettings = load(new WorldSettings(), getConfiguration().getConfigurationSection("island.overworld"));
        netherSettings = load(new WorldSettings(), getConfiguration().getConfigurationSection("island.nether"));
    }
}
