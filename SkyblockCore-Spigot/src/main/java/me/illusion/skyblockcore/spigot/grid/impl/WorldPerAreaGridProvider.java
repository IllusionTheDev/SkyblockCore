package me.illusion.skyblockcore.spigot.grid.impl;

import me.illusion.cosmos.grid.CosmosGrid;
import me.illusion.cosmos.grid.impl.WorldPerAreaGrid;
import me.illusion.cosmos.world.VoidGenerator;
import me.illusion.cosmos.world.pool.WorldPoolSettings;
import me.illusion.skyblockcore.common.utilities.time.Time;
import me.illusion.skyblockcore.common.utilities.time.TimeParser;
import me.illusion.skyblockcore.spigot.grid.SkyblockGridProvider;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;

/**
 * Base provider for a {@link WorldPerAreaGrid}.
 */
public class WorldPerAreaGridProvider implements SkyblockGridProvider {

    @Override
    public CosmosGrid provide(ConfigurationSection section) {
        int maxCachedWorlds = section.getInt("max-cached-worlds", 10);
        int maxUnloadedWorlds = section.getInt("max-unloaded-worlds", 25);
        int preGeneratedWorlds = section.getInt("pre-generated-worlds", 5);

        ChunkGenerator generator = new VoidGenerator(); // The API for getting chunk generators requires a world name, and we're using a world pool.

        Vector spawnLocation = getVector(section.getConfigurationSection("spawn-location"));

        int batchDelayTicks = section.getInt("batch-delay-ticks", 20);
        Time deletionDelay = TimeParser.parse(section.getString("deletion-delay", "10 seconds"));

        WorldPoolSettings settings = WorldPoolSettings.builder()
            .maxCachedWorlds(maxCachedWorlds)
            .maxUnloadedWorlds(maxUnloadedWorlds)
            .preGeneratedWorlds(preGeneratedWorlds)
            .chunkGenerator(generator)
            .spawnLocation(spawnLocation)
            .batchDelayTicks(batchDelayTicks)
            .deletionDelay(SkyblockBukkitAdapter.asCosmosTime(deletionDelay))
            .build();

        return new WorldPerAreaGrid(settings);
    }

    private Vector getVector(ConfigurationSection section) {
        if (section == null) {
            return new Vector(0, 128, 0);
        }

        return new Vector(section.getInt("x", 0), section.getInt("y", 128), section.getInt("z", 0));
    }
}
