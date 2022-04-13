package me.illusion.skyblockcore.spigot.island.world;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class EmptyWorldGenerator extends ChunkGenerator {

    private final SkyblockPlugin main;

    public EmptyWorldGenerator(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        return this.createChunkData(world);
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return main.getFiles().getIslandConfig().getSpawnPoint().toLocation(world);
    }
}
