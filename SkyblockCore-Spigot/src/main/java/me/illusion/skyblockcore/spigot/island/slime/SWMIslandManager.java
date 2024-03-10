package me.illusion.skyblockcore.spigot.island.slime;

import com.infernalsuite.aswm.api.world.SlimeWorld;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.scheduler.ThreadContext;
import me.illusion.skyblockcore.common.utilities.time.Time;
import me.illusion.skyblockcore.server.event.island.SkyblockIslandLoadEvent;
import me.illusion.skyblockcore.server.event.island.SkyblockIslandUnloadEvent;
import me.illusion.skyblockcore.server.island.AbstractIslandManager;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.util.SkyblockCuboid;
import me.illusion.skyblockcore.server.util.SkyblockLocation;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.island.slime.setting.SlimeSettings;
import me.illusion.skyblockcore.spigot.island.slime.wrapper.SlimeAPI;
import me.illusion.skyblockcore.spigot.island.slime.wrapper.impl.SlimeAPIImpl;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Manages islands. The lifecycle of an island is tied to a CosmosSession, which means that if the session is destroyed, the island is destroyed.
 */
public class SWMIslandManager extends AbstractIslandManager {

    private final SlimeSettings settings;
    private final SlimeAPI slimeAPI;

    private final Map<UUID, IslandUnloadingRequest> unloadRequests = new ConcurrentHashMap<>();

    public SWMIslandManager(ConfigurationSection section, SkyblockSpigotPlugin plugin) {
        super(plugin);

        this.settings = new SlimeSettings(section);
        this.slimeAPI = new SlimeAPIImpl(settings);
    }

    /**
     * Loads an island from IslandData
     *
     * @param data The data of the island
     * @return The loaded island
     */
    @Override
    public CompletableFuture<SkyblockIsland> loadIsland(IslandData data) {
        UUID islandId = data.getIslandId();

        SkyblockIsland cached = getLoadedIsland(islandId);

        if (cached != null) { // Idiots
            return CompletableFuture.completedFuture(cached);
        }

        String id = islandId.toString();

        return register(slimeAPI.getOrLoadWorld(id).thenApply(slimeWorld -> {
            if (slimeWorld == null) {
                throw new IllegalStateException("Slime World not found, database is corrupted!");
            }

            return loadFromTemplate(islandId, data, slimeWorld);
        }));
    }

    /**
     * Loads an island from a template
     *
     * @param template The template
     * @return The loaded island
     */
    @Override
    public CompletableFuture<SkyblockIsland> createIsland(String template, UUID profileId) {
        UUID islandId = UUID.randomUUID();
        IslandData data = new IslandData(islandId, profileId);

        return register(slimeAPI.getOrLoadOrCopyWorld(islandId.toString(), template).thenApply(slimeWorld -> {
            if (slimeWorld == null) {
                throw new IllegalStateException("Slime World not found, database is corrupted!");
            }

            return loadFromTemplate(islandId, data, slimeWorld);
        }));
    }

    /**
     * Forces an island to unload
     *
     * @param islandId The island's id
     * @param save     Whether or not to save the island
     * @return A future
     */
    @Override
    public CompletableFuture<Void> forceUnloadIsland(UUID islandId, boolean save) {
        unloadingIslands.add(islandId);
        return register(slimeAPI.unloadWorld(islandId.toString()).thenRun(() -> removeInternal(islandId)));
    }

    /**
     * Requests to unload an island after a certain delay. If the island is requested to be loaded before the delay is over, the request is cancelled.
     *
     * @param islandId    The island's id
     * @param save        Whether or not to save the island
     * @param unloadDelay The delay
     * @return A future
     */
    @Override
    public CompletableFuture<Boolean> requestUnloadIsland(UUID islandId, boolean save, Time unloadDelay) {
        unloadingIslands.add(islandId);

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        UUID taskId = platform.getScheduler().scheduleOnce(ThreadContext.ASYNC, () -> {
            if (future.isDone() || future.isCancelled()) {
                return;
            }

            if (unloadingIslands.remove(islandId)) {
                forceUnloadIsland(islandId, save);
                future.complete(true);
            } else {
                future.complete(false);
            }
        }, unloadDelay);

        unloadRequests.put(islandId, new IslandUnloadingRequest(islandId, taskId, future));

        return future;
    }

    /**
     * Disables the island manager, unloading all islands
     *
     * @param save  Whether or not to save the islands
     * @param async Set this to FALSE if you're disabling the plugin, otherwise set it to TRUE. You can't use the scheduler on shutdown
     * @return A future
     */
    @Override
    public CompletableFuture<Void> disable(boolean save, boolean async) {
        // We could save islands here, but at the moment island data isn't modified at all, so it's not necessary.

        for (UUID islandId : loadedIslands.keySet()) {
            forceUnloadIsland(islandId, save);
        }

        return CompletableFuture.completedFuture(null);
    }


    private SkyblockIsland loadFromTemplate(UUID islandId, IslandData data, SlimeWorld slimeWorld) {
        World bukkitWorld = Bukkit.getWorld(slimeWorld.getName());
        SkyblockLocation pasteLocation = SkyblockBukkitAdapter.toSkyblockLocation(bukkitWorld.getSpawnLocation());

        SkyblockLocation point1 = pasteLocation.add(settings.getRadius(), 0, settings.getRadius()).setY(0);
        SkyblockLocation point2 = pasteLocation.subtract(settings.getRadius(), 0, settings.getRadius()).setY(255);

        SkyblockCuboid bounds = new SkyblockCuboid(point1, point2);

        SkyblockIsland island = new SkyblockIsland(data, pasteLocation, bounds);
        loadedIslands.put(islandId, island);

        platform.getEventManager().callEvent(new SkyblockIslandLoadEvent(island));

        return island;

    }


    /**
     * Removes an island from the manager internally
     *
     * @param islandId The island's id
     */
    private void removeInternal(UUID islandId) {
        SkyblockIsland island = loadedIslands.remove(islandId);

        if (island != null) {
            platform.getEventManager().callEvent(new SkyblockIslandUnloadEvent(island));
        }

        unloadingIslands.remove(islandId);
    }

    private void cancelPendingUnload(UUID islandId) {
        IslandUnloadingRequest request = unloadRequests.remove(islandId);

        if (request != null) {
            platform.getScheduler().cancel(request.getTaskId());
            request.getFuture().complete(false);
        }
    }

    private static class IslandUnloadingRequest {

        private final UUID islandId;
        private final UUID taskId;
        private final CompletableFuture<Boolean> future;

        public IslandUnloadingRequest(UUID islandId, UUID taskId, CompletableFuture<Boolean> future) {
            this.islandId = islandId;
            this.taskId = taskId;
            this.future = future;
        }

        public UUID getIslandId() {
            return islandId;
        }

        public UUID getTaskId() {
            return taskId;
        }

        public CompletableFuture<Boolean> getFuture() {
            return future;
        }
    }

}
