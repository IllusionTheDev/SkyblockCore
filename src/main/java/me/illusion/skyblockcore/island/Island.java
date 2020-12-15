package me.illusion.skyblockcore.island;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@AllArgsConstructor
public class Island {

    private final Location pointOne;
    private final Location pointTwo;
    private final Location center;

    private final IslandData data;

    public void cleanIsland() {
        World world = getCenter().getWorld();

        CuboidRegion region = new CuboidRegion(FaweAPI.getWorld(getCenter().getWorld().getName()),
                new Vector(pointOne.getBlockX(), pointOne.getBlockY(), pointOne.getBlockZ()),
                new Vector(pointTwo.getBlockX(), pointTwo.getBlockY(), pointTwo.getBlockZ()));

        region.getChunks().forEach((c) -> world.regenerateChunk(c.getBlockX(), c.getBlockZ()));
    }
}
