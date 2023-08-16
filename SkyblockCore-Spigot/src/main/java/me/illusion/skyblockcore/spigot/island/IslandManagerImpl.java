package me.illusion.skyblockcore.spigot.island;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.cosmos.session.CosmosSession;
import me.illusion.cosmos.template.TemplatedArea;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.utilities.time.Time;
import me.illusion.skyblockcore.server.event.island.SkyblockIslandLoadEvent;
import me.illusion.skyblockcore.server.event.island.SkyblockIslandUnloadEvent;
import me.illusion.skyblockcore.server.island.AbstractIslandManager;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.util.SkyblockCuboid;
import me.illusion.skyblockcore.server.util.SkyblockLocation;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;

/**
 * Manages islands. The lifecycle of an island is tied to a CosmosSession, which means that if the session is destroyed, the island is destroyed.
 */
public class IslandManagerImpl extends AbstractIslandManager {

    private final SkyblockCosmosSetup cosmosSetup;

    public IslandManagerImpl(SkyblockSpigotPlugin plugin) {
        super(plugin);

        this.cosmosSetup = plugin.getCosmosSetup();

        new IllegalIslandUnloadCatcher(this,
            plugin).register(); // This is a listener that catches when an island is unloaded illegally, through Cosmos directly.
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

        TemplatedArea cachedArea = cosmosSetup.getTemplateCache().get(id);

        if (cachedArea != null) {
            return register(loadFromTemplate(islandId, data, cachedArea));
        }

        return register(cosmosSetup.getIslandContainer().fetchTemplate(id).thenCompose(template -> {
            if (template == null) {
                throw new IllegalStateException("Template not found, database is corrupted!");
            }

            return loadFromTemplate(islandId, data, template);
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
        TemplatedArea cachedArea = cosmosSetup.getTemplateCache().get(template);

        if (cachedArea == null) {
            throw new IllegalStateException("Template not found, improper setup!");
        }

        UUID islandId = UUID.randomUUID();
        IslandData data = new IslandData(islandId, profileId);

        return register(database.saveIslandData(data).thenCompose(irrelevant -> loadFromTemplate(islandId, data, cachedArea)));
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
        return register(cosmosSetup.getSessionHolder().unloadSession(islandId, save, true).thenRun(() -> removeInternal(islandId)));
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

        return register(
            cosmosSetup.getSessionHolder().unloadAutomaticallyIn(SkyblockBukkitAdapter.asCosmosTime(unloadDelay), islandId, save).thenApply(unloaded -> {
                if (Boolean.TRUE.equals(unloaded)) {
                    removeInternal(islandId);
                }

                unloadingIslands.remove(islandId);
                return unloaded;
            }));
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

        return register(cosmosSetup.getSessionHolder().unloadAll(save, async));
    }

    /**
     * Forces all islands to unload
     *
     * @param islandId The island's id
     * @param data     The island's data
     * @param area     The island's template
     * @return A future
     */
    private CompletableFuture<SkyblockIsland> loadFromTemplate(UUID islandId, IslandData data, TemplatedArea area) {
        return register(cosmosSetup.getSessionHolder().loadOrCreateSession(islandId, area).thenApply(session -> {

            SkyblockLocation pasteLocation = SkyblockBukkitAdapter.toSkyblockLocation(session.getPastedArea().getPasteLocation());
            SkyblockCuboid bounds = SkyblockBukkitAdapter.toSkyblockCuboid(session.getPastedArea().getDimensions());

            SkyblockIsland island = new SkyblockIsland(data, pasteLocation, bounds);
            loadedIslands.put(islandId, island);

            platform.getEventManager().callEvent(new SkyblockIslandLoadEvent(island));

            return island;
        }));
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

    /**
     * Performs sanity checks when a session is removed
     *
     * @param session The session
     */
    void registerRemoved(CosmosSession session) {
        UUID sessionId = session.getUuid();

        if (!unloadingIslands.contains(sessionId) && loadedIslands.containsKey(sessionId)) {
            throw new IllegalStateException("Session was removed without being unloaded! Possible API misuse!");
        }
    }


}
