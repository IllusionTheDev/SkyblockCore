package me.illusion.skyblockcore.spigot.file;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.file.path.ExtendedYMLBase;
import me.illusion.skyblockcore.spigot.file.settings.WorldSettings;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

@Getter
public class IslandConfig extends ExtendedYMLBase {

    private final int worldCount;
    private final String pastingSelection;
    private final WorldSettings overworldSettings;
    private final WorldSettings netherSettings;
    private final Vector spawnPoint;
    private final int inviteTimeoutSeconds;
    
    public IslandConfig(JavaPlugin plugin) {
        super(plugin, "island-config.yml");

        FileConfiguration config = getConfiguration();

        pastingSelection = config.getString("island.pasting-type");
        worldCount = config.getInt("island.island-worlds", 25);

        overworldSettings = load(new WorldSettings(), config.getConfigurationSection("island.overworld"));
        netherSettings = load(new WorldSettings(), config.getConfigurationSection("island.nether"));

        double x = config.getDouble("island.spawn-point.x", 256.5);
        double y = config.getDouble("island.spawn-point.y", 128.5);
        double z = config.getDouble("island.spawn-point.z", 256.5);

        spawnPoint = new Vector(x, y, z);

        inviteTimeoutSeconds = config.getInt("island.invite-timeout", 60);
    }
}
