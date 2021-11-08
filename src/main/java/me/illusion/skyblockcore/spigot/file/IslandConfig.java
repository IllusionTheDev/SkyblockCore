package me.illusion.skyblockcore.spigot.file;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.file.path.ExtendedYMLBase;
import me.illusion.skyblockcore.spigot.file.settings.WorldSettings;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

@Getter
public class IslandConfig extends ExtendedYMLBase {

    private final int worldCount;
    private final String pastingSelection;
    private final WorldSettings overworldSettings;
    private final WorldSettings netherSettings;
    private final Vector spawnPoint;

    public IslandConfig(JavaPlugin plugin) {
        super(plugin, "island-config.yml");

        pastingSelection = getConfiguration().getString("island.pasting-type");
        worldCount = getConfiguration().getInt("island.island-worlds", 25);

        overworldSettings = load(new WorldSettings(), getConfiguration().getConfigurationSection("island.overworld"));
        netherSettings = load(new WorldSettings(), getConfiguration().getConfigurationSection("island.nether"));

        double x = getConfiguration().getDouble("island.spawn-point.x", 256.5);
        double y = getConfiguration().getDouble("island.spawn-point.y", 128.5);
        double z = getConfiguration().getDouble("island.spawn-point.z", 256.5);

        spawnPoint = new Vector(x, y, z);
    }
}
