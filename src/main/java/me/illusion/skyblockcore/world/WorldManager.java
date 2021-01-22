package me.illusion.skyblockcore.world;

import me.illusion.skyblockcore.CorePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldManager {

    private final Map<Integer, UUID> loadedIslands = new HashMap<>();

    public WorldManager(CorePlugin main) {
        for (int i = 1; i <= main.getIslandConfig().getWorldCount(); i++)
            loadedIslands.put(i, null);
    }

    public String assignWorld(UUID island) {
        for (Map.Entry<Integer, UUID> entry : loadedIslands.entrySet())
            if (entry.getValue() == null)
                return "SkyblockWorld" + entry.getKey();

        return null;
    }

    public void unregister(int id) {
        loadedIslands.put(id, null);
    }
}
