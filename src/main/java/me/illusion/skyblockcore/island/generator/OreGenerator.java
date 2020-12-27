package me.illusion.skyblockcore.island.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.illusion.skyblockcore.sql.serialized.SerializedLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.Serializable;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class OreGenerator implements Serializable {

    private final transient Location center;
    private final SerializedLocation relativeLocation;
    private final OreGeneratorType type;
    private long nextGenerationEpochSecond;

    /**
     * Check if the OreGenerator is at a specific location
     *
     * @param loc - The location to compare to
     * @return TRUE if the locations match, FALSE otherwise
     */
    public boolean isAtLocation(Location loc) {
        return center.add(relativeLocation.getLocation().getX(), 0, relativeLocation.getLocation().getZ()).equals(loc);
    }

    /**
     * Internal tick method
     */
    public void tick() {
        long current = Instant.now().getEpochSecond();

        if (current < nextGenerationEpochSecond)
            return;

        Material target = type.getEndMaterial();
        Block b = center.add(relativeLocation.getLocation().getX(), 0, relativeLocation.getLocation().getZ()).getBlock();

        if (b.getType() != target)
            return;

        b.setType(target);
        nextGenerationEpochSecond = -1;
    }

    /**
     * Handles block breaking
     */
    public void handleBreak() {
        Block b = relativeLocation.getLocation().add(center.getX(), 0, center.getZ()).getBlock();

        b.setType(type.getCooldownMaterial());
        nextGenerationEpochSecond = Instant.now().getEpochSecond() + type.getCooldownSeconds();
    }

}

