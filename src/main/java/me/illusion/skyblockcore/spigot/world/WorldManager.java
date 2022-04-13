package me.illusion.skyblockcore.spigot.world;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class WorldManager implements Listener {

    private final Map<String, Consumer<World>> saveEvents = new HashMap<>();
    private final Map<String, Consumer<World>> loadEvents = new ConcurrentHashMap<>();
    private final Map<String, Consumer<World>> unloadEvents = new ConcurrentHashMap<>();

    private final Map<String, UUID> loadedIslands = new HashMap<>();

    private final SkyblockPlugin main;

    public WorldManager(SkyblockPlugin main) {
        this.main = main;

        for (int index = 1; index <= main.getFiles().getIslandConfig().getWorldCount(); index++) {
            loadedIslands.put("skyblockworld" + index, null);

            if (new File(Bukkit.getWorldContainer() + File.separator + "skyblockworld" + index).exists())
                continue;

            main.setupWorld("skyblockworld" + index);
            Bukkit.unloadWorld("skyblockworld" + index, true);
        }

        Bukkit.getPluginManager().registerEvents(this, main);

    }

    public String assignWorld(UUID islandId) {
        for (Map.Entry<String, UUID> entry : loadedIslands.entrySet())
            if (entry.getValue() == null) {
                loadedIslands.put(entry.getKey(), islandId);
                return entry.getKey();

            }

        return null;
    }

    public void unregister(String world) {
        loadedIslands.put(world, null);
        //Bukkit.unloadWorld(world, false);
    }

    public void whenNextSave(Consumer<World> worldConsumer, String worldname) {
        saveEvents.put(worldname, saveEvents.getOrDefault(worldname, (world) -> {
        }).andThen(worldConsumer));
    }

    public void whenNextLoad(Consumer<World> worldConsumer, String worldname) {
        loadEvents.put(worldname.toLowerCase(Locale.ROOT), loadEvents.getOrDefault(worldname.toLowerCase(Locale.ROOT), (world) -> {
        }).andThen(worldConsumer));
    }

    public void whenNextUnload(Consumer<World> worldConsumer, String worldname) {
        unloadEvents.put(worldname.toLowerCase(Locale.ROOT), unloadEvents.getOrDefault(worldname.toLowerCase(Locale.ROOT), (world) -> {
        }).andThen(worldConsumer));
    }

    public boolean isSkyblockWorld(String name) {
        return loadedIslands.containsKey(name);
    }

    @EventHandler
    private void onSave(WorldSaveEvent event) {
        World world = event.getWorld();
        String name = world.getName();

        Consumer<World> action = saveEvents.remove(name);

        if (action != null)
            action.accept(world);
    }

    @EventHandler
    private void onLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        String name = world.getName().toLowerCase(Locale.ROOT);

        Consumer<World> action = loadEvents.remove(name);

        if (action != null)
            action.accept(world);
    }

    @EventHandler
    private void onUnload(WorldUnloadEvent event) {
        World world = event.getWorld();
        String name = world.getName().toLowerCase(Locale.ROOT);

        Consumer<World> action = unloadEvents.remove(name);

        if (action != null)
            action.accept(world);
    }
}
