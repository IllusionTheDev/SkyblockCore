package me.illusion.skyblockcore.island.generator;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum OreGeneratorType {
    COBBLESTONE(1, Material.COBBLESTONE, 0, 10),
    COAL(2, Material.COAL_ORE, 15, 60),
    IRON(3, Material.IRON_ORE, 7, 3600),
    LAPIZ(4, Material.LAPIS_ORE, 11, 600),
    GOLD(5, Material.GOLD_ORE, 1, 10000),
    DIAMOND(6, Material.DIAMOND_ORE, 9, 20000),
    QUARTZ(7, Material.QUARTZ_ORE, 14, 5000);

    private static final OreGeneratorType[] VALUES = {
            COBBLESTONE,
            COAL,
            IRON,
            LAPIZ,
            GOLD,
            DIAMOND,
            QUARTZ
    };

    private final int id;
    private final Material endMaterial;
    private final Material cooldownMaterial = Material.STAINED_CLAY;
    private final short color;
    private final long cooldownSeconds;

    OreGeneratorType(int id, Material endMaterial, int color, long cooldownSeconds) {
        this.id = id;
        this.endMaterial = endMaterial;
        this.color = (short) color;
        this.cooldownSeconds = cooldownSeconds;
    }

    /**
     * Obtains an OreGeneratorType from its ID
     *
     * @param id - The ID to mathc
     * @return an OreGeneratorType if it matches, NULL otherwise
     */
    public static OreGeneratorType fromId(int id) {
        for (OreGeneratorType type : VALUES)
            if (type.getId() == id)
                return type;

        return null;
    }

    public static OreGeneratorType[] getValues() {
        return VALUES;
    }
}
