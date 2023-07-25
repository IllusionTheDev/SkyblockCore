package me.illusion.skyblockcore.spigot.grid.impl;

import java.util.UUID;
import me.illusion.cosmos.grid.CosmosGrid;
import me.illusion.cosmos.grid.impl.SingleWorldGrid;
import me.illusion.cosmos.world.VoidGenerator;
import me.illusion.skyblockcore.spigot.grid.SkyblockGridProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.ChunkGenerator;

public class SingleWorldGridProvider implements SkyblockGridProvider {

    @Override
    public CosmosGrid provide(ConfigurationSection section) {
        // I know this looks like a mess but bear with me
        String worldId = getWorldName(section);
        World world = Bukkit.getWorld(worldId);

        ChunkGenerator generator = getGenerator(section, worldId);

        if (world == null) {
            // World not found, let's just create one
            world = Bukkit.createWorld(new WorldCreator(worldId).generator(generator));
        }

        int distanceBetweenIslands = section.getInt("distance-between-islands", 1000);
        int yLevel = section.getInt("y-level", 128);

        return SingleWorldGrid.builder()
            .worldId(world.getUID())
            .distanceBetweenAreas(distanceBetweenIslands)
            .baseYLevel(yLevel)
            .chunkGenerator(generator)
            .build();
    }

    private String getWorldName(ConfigurationSection section) {
        String worldId = section.getString("world-id");
        World world;

        if (worldId != null) {
            world = Bukkit.getWorld(UUID.fromString(worldId));
        } else {
            world = Bukkit.getWorld(section.getString("world-name", "skyblock-world"));
        }

        worldId = world == null ? "skyblock-world" : world.getName(); // If world is null, we'll just use the default name

        return worldId;
    }

    private ChunkGenerator getGenerator(ConfigurationSection section, String worldId) {
        ChunkGenerator generator = null;

        String generatorId = section.getString("generator", "builtin");

        if (!generatorId.equalsIgnoreCase("builtin")) {
            generator = WorldCreator.getGeneratorForName(worldId, generatorId, Bukkit.getConsoleSender());
        }

        if (generator == null) {
            generator = new VoidGenerator();
        }

        return generator;
    }

}
