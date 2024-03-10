package me.illusion.skyblockcore.spigot.island.slime.wrapper.impl;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.exceptions.WorldAlreadyExistsException;
import com.infernalsuite.aswm.api.exceptions.WorldLockedException;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.spigot.island.slime.setting.SlimeSettings;
import me.illusion.skyblockcore.spigot.island.slime.wrapper.SlimeAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class SlimeAPIImpl implements SlimeAPI {

    public static final SlimePropertyMap EMPTY_PROPERTIES = new SlimePropertyMap();
    private final SlimePlugin plugin;
    private final SlimeSettings settings;

    public SlimeAPIImpl(SlimeSettings settings) {
        this.plugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        this.settings = settings;
    }

    @Override
    public SlimePlugin getPlugin() {
        return plugin;
    }

    @Override
    public SlimeLoader getPreferredLoader() {
        return this.getLoader(settings.getPreferredLoader());
    }

    @Override
    public SlimeLoader getLoader(String name) {
        return plugin.getLoader(name);
    }

    @Override
    public CompletableFuture<SlimeWorld> loadWorld(String name) {
        SlimeLoader loader = getPreferredLoader();

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!loader.worldExists(name)) {
                    return null;
                }

                return plugin.loadWorld(loader, name, false, EMPTY_PROPERTIES);
            } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldLockedException e) {
                e.printStackTrace(); // if this happens we're screwed
            }

            return null;
        }).thenApplyAsync(this::loadWorldSync, MainThreadExecutor.INSTANCE);
    }

    @Override
    public CompletableFuture<SlimeWorld> copyWorld(SlimeWorld world, String newName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return world.clone(newName, getPreferredLoader());
            } catch (WorldAlreadyExistsException | IOException e) {
                throw new RuntimeException(e);
            }
        }).thenApplyAsync(this::loadWorldSync, MainThreadExecutor.INSTANCE);
    }

    @Override
    public CompletableFuture<Void> unloadWorld(String name) {
        if (!Bukkit.isPrimaryThread()) {
            return CompletableFuture.runAsync(() -> unloadWorld(name), MainThreadExecutor.INSTANCE);
        }

        boolean success = Bukkit.unloadWorld(name, true);

        if (!success) {
            return CompletableFuture.failedFuture(new RuntimeException("Failed to unload world " + name));
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> saveWorld(String name) {
        World world = Bukkit.getWorld(name);

        if (world == null) {
            return CompletableFuture.completedFuture(null);
        }

        world.save();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<SlimeWorld> getOrLoadWorld(String name) {
        SlimeWorld world = plugin.getWorld(name);

        if (world != null) {
            return CompletableFuture.completedFuture(world);
        }

        return loadWorld(name);
    }

    @Override
    public CompletableFuture<Void> deleteWorld(String name) {
        return CompletableFuture.runAsync(() -> {
            try {
                getPreferredLoader().deleteWorld(name);
            } catch (UnknownWorldException | IOException e) {
                e.printStackTrace(); // if this happens we're screwed
            }
        }, MainThreadExecutor.INSTANCE);
    }

    private SlimeWorld loadWorldSync(SlimeWorld world) {
        if (world == null) {
            return null;
        }

        try {
            return plugin.loadWorld(world);
        } catch (UnknownWorldException | WorldLockedException | IOException e) {
            e.printStackTrace(); // if this happens we're screwed
        }

        return world;
    }

}
