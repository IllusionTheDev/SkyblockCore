package me.illusion.skyblockcore.spigot.world;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldManager {

    private final Map<String, UUID> loadedIslands = new HashMap<>();

    public WorldManager(SkyblockPlugin main) {
        for (int i = 1; i <= main.getIslandConfig().getWorldCount(); i++) {
            loadedIslands.put("SkyblockWorld" + i, null);
            main.setupWorld("SkyblockWorld" + i);
            Bukkit.unloadWorld("SkyblockWorld", false);
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
        Bukkit.unloadWorld(world, false);
    }
}
