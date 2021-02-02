package me.illusion.skyblockcore.spigot.world;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class WorldManager implements Listener {

    private final Map<String, Consumer<World>> saveEvents = new HashMap<>();
    private final Map<String, UUID> loadedIslands = new HashMap<>();

    public WorldManager(SkyblockPlugin main) {
        for (int i = 1; i <= main.getIslandConfig().getWorldCount(); i++) {
            loadedIslands.put("SkyblockWorld" + i, null);
            main.setupWorld("SkyblockWorld" + i);
            Bukkit.unloadWorld("SkyblockWorld" + i, true);
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

    public void whenNextSave(Consumer<World> worldConsumer, String worldname) {
        saveEvents.put(worldname, worldConsumer);
    }

    @EventHandler
    private void onSave(WorldSaveEvent e) {
        World world = e.getWorld();
        String name = world.getName();

        Consumer<World> action = saveEvents.remove(name);

        if (action != null)
            action.accept(world);
    }
}
