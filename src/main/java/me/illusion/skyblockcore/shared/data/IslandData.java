package me.illusion.skyblockcore.shared.data;

import lombok.Getter;
import lombok.Setter;
import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.shared.utilities.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class IslandData implements SkyblockSerializable {

    private UUID id;
    @Setter
    private SerializedFile[] islandSchematic;
    private transient List<UUID> users = new ArrayList<>();
    private UUID owner;

    private String serialized;

    @Setter
    private SerializedLocation spawnPointRelativeToCenter;

    public IslandData(UUID id, UUID ownerId) {
        this.id = id;
        this.owner = ownerId;
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

    public void removeUser(UUID userId) {
        users.remove(userId);
        serialized = serialized.replace(userId.toString(), "");

        // remove double spaces
        while (serialized.contains("  ")) {
            serialized = serialized.replace("  ", " ");
        }
    }

    @Override
    public void load(Map<String, Object> map) {
        String idString = map.getOrDefault("id", UUID.randomUUID()).toString();
        String ownerString = map.getOrDefault("owner", UUID.randomUUID()).toString();
        String usersString = map.getOrDefault("users", "").toString();
        String spawnPointRelativeToCenterString = map.getOrDefault("spawnPointRelativeToCenter", "").toString();

        this.id = UUID.fromString(idString);
        this.owner = UUID.fromString(ownerString);
        this.serialized = usersString;
        this.spawnPointRelativeToCenter = new SerializedLocation();

        spawnPointRelativeToCenter.setFormat(spawnPointRelativeToCenterString);
    }

    @Override
    public void save(Map<String, Object> map) {
        map.put("id", id.toString());
        map.put("owner", owner.toString());
        map.put("users", serialized);
        map.put("spawnPointRelativeToCenter", spawnPointRelativeToCenter.getFormat());
    }
}
