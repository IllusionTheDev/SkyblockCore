package me.illusion.skyblockcore.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.illusion.skyblockcore.island.generator.OreGenerator;
import me.illusion.utilities.storage.StringUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class IslandData implements Serializable {

    private final UUID id;
    private final File islandSchematic;
    private final String serialized;
    private final UUID owner;
    private final List<OreGenerator> oreGenerators;
    @Setter
    private transient Island island;

    /**
     * Registers an OreGenerator
     *
     * @param generator - The ore generator
     */
    public void addGenerator(OreGenerator generator) {
        oreGenerators.add(generator);
    }

    /**
     * Gets its users, as a UUID list
     *
     * @return the list of UUIDs
     */
    public List<UUID> getUsers() {
        List<UUID> list = new ArrayList<>();

        String[] split = StringUtil.split(serialized, ' ');

        for (String str : split)
            list.add(UUID.fromString(str));

        return list;
    }
}
