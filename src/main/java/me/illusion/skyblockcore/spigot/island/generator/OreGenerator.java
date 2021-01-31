package me.illusion.skyblockcore.spigot.island.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.illusion.skyblockcore.spigot.sql.serialized.SerializedLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.Serializable;
import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class OreGenerator implements Serializable {

    private final SerializedLocation relativeLocation;
    private final OreGeneratorType type;
    private long nextGenerationEpochSecond;

    private transient Location center = null;
    private transient Block block;
    private transient Location location;

    public void setCenter(Location center) {
        this.center = center;
        this.location = center.add(relativeLocation.getLocation().getX(), 0, relativeLocation.getLocation().getZ());
        this.block = location.getBlock();
    }

    /**
     * Check if the OreGenerator is at a specific location
     *
     * @param loc - The location to compare to
     * @return TRUE if the locations match, FALSE otherwise
     */
    public boolean isAtLocation(Location loc) {
        return location.equals(loc);
    }

    /**
     * Internal tick method
     */
    public void tick() {
        long current = Instant.now().getEpochSecond();

        if (current < nextGenerationEpochSecond)
            return;

        Material target = type.getEndMaterial();

        if (block.getType() != target)
            return;

        block.setType(target);
        nextGenerationEpochSecond = -1;
    }

    /**
     * Handles block breaking
     */
    public void handleBreak() {
        block.setType(type.getCooldownMaterial());
        nextGenerationEpochSecond = Instant.now().getEpochSecond() + type.getCooldownSeconds();
    }

}

