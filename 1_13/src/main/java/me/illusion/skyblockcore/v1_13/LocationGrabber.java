package me.illusion.skyblockcore.v1_13;

import com.google.common.collect.Sets;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LocationGrabber {

    public static CompletableFuture<Location> getSafeLocation(Location origin) {
        ChunkSnapshot snapshot = origin.getChunk().getChunkSnapshot();

        return CompletableFuture.supplyAsync(() -> {
            Set<Location> locations = Sets.newConcurrentHashSet();

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        Material type = snapshot.getBlockType(x, y, z);

                        if (type.isBlock() || type.isSolid() || type.isOccluding())
                            continue;

                        Material above = snapshot.getBlockType(x, y + 1, z);

                        if (above.isBlock() || above.isSolid() || above.isOccluding())
                            continue;

                        locations.add(new Location(origin.getWorld(), origin.getBlockX() + x, origin.getBlockY() + y, origin.getBlockZ() + z));
                    }
                }
            }

            Location closest = null;

            for (Location loc : locations) {
                if (closest == null) {
                    closest = loc;
                }

                if (loc.distanceSquared(origin) < closest.distanceSquared(origin)) {
                    closest = loc;
                }
            }

            return closest;
        });
    }
}
