package me.illusion.skyblockcore.shared.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.shared.utilities.StringUtil;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.island.generator.OreGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class IslandData implements Serializable {

    private final UUID id;
    @Setter
    private SerializedFile[] islandSchematic;
    private transient List<UUID> users = new ArrayList<>();
    private final UUID owner;
    private final List<OreGenerator> oreGenerators;

    @Setter
    private transient Island island;
    private String serialized;


    /**
     * Registers an OreGenerator
     *
     * @param generator - The ore generator
     */
    public void addGenerator(OreGenerator generator) {
        oreGenerators.add(generator);
    }

    /**
     * Adds a user to the island
     *
     * @param uuid - The user's UUID
     */
    public void addUser(UUID uuid) {
        users.add(uuid);
        serialized = serialized + " " + uuid.toString();
    }

    /**
     * Gets its users, as a UUID list
     * Ensures a singleton-style
     *
     * @return the list of UUIDs
     */
    public List<UUID> getUsers() {
        if (users == null)
            users = new ArrayList<>();

        if (!users.isEmpty())
            return users;

        List<UUID> list = new ArrayList<>();

        String[] split = StringUtil.split(serialized, ' ');
        System.out.println(serialized);

        for (String str : split) {
            if (str.equalsIgnoreCase("null"))
                continue;
            list.add(UUID.fromString(str));
        }

        users.addAll(list);
        return list;
    }
}
