package me.illusion.skyblockcore.world;

import me.illusion.skyblockcore.CorePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldManager {

    private final Map<String, UUID> loadedIslands = new HashMap<>();

    public WorldManager(CorePlugin main) {
        for (int i = 1; i <= main.getIslandConfig().getWorldCount(); i++) {
            loadedIslands.put("SkyblockWorld" + i, null);
            main.setupWorld("SkyblockWorld" + i);
        }

    }

    public String assignWorld() {
        for (Map.Entry<String, UUID> entry : loadedIslands.entrySet())
            if (entry.getValue() == null)
                return entry.getKey();

        return null;
    }

    public void unregister(String world) {
        loadedIslands.put(world, null);
    }
}
