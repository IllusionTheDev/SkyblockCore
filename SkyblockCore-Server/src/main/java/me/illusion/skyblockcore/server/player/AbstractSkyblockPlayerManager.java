package me.illusion.skyblockcore.server.player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.Setter;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.common.storage.profiles.SkyblockProfileStorage;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerJoinEvent;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerQuitEvent;

/**
 * Represents a base implementation of {@link SkyblockPlayerManager}.
 */
public abstract class AbstractSkyblockPlayerManager implements SkyblockPlayerManager {

    protected final SkyblockPlatform platform;
    protected final SkyblockProfileStorage database;

    private final Map<UUID, UUID> profileMap = new ConcurrentHashMap<>(); // Player ID -> Profile ID
    private final Map<UUID, SkyblockPlayer> playerIdMap = new ConcurrentHashMap<>();

    @Setter
    private Function<UUID, UUID> profileMappingFunction = Function.identity(); // Player ID -> Player ID

    protected AbstractSkyblockPlayerManager(SkyblockPlatform platform) {
        this.platform = platform;
        this.database = platform.getDatabaseRegistry().getStorage(SkyblockProfileStorage.class);
    }

    // Player management stuff

    protected void handleJoin(UUID playerId) {
        System.out.println("Handling join for " + playerId);
        cacheProfileId(playerId).thenAccept(profileId -> {
            SkyblockPlayer player = createPlayer(playerId);
            playerIdMap.put(profileId, player);
            platform.getEventManager().callEvent(new SkyblockPlayerJoinEvent(player));
        });
    }

    protected void handleQuit(UUID playerId) {
        UUID profileId = getCachedProfileId(playerId);

        if (profileId == null) {
            return;
        }

        SkyblockPlayer player = getPlayerFromProfile(profileId);

        if (player == null) {
            return;
        }

        platform.getEventManager().callEvent(new SkyblockPlayerQuitEvent(player));
        playerIdMap.remove(profileId);
    }

    protected abstract SkyblockPlayer createPlayer(UUID playerId);

    // Internal player management

    @Override
    public SkyblockPlayer getPlayer(UUID playerId) {
        UUID profileId = getCachedProfileId(playerId);

        if (profileId == null) {
            return null;
        }

        return getPlayerFromProfile(profileId);
    }

    @Override
    public SkyblockPlayer getPlayerFromProfile(UUID profileId) {
        return playerIdMap.get(profileId);
    }

    // Internal profile stuff

    private CompletableFuture<UUID> cacheProfileId(UUID playerId) {
        return fetchProfileId(playerId).thenCompose(profileId -> {
            System.out.println("Profile ID for " + playerId + " is " + profileId);

            if (profileId == null) {
                profileId = createProfileId(playerId);
                setIdInternally(playerId, profileId);

                UUID finalProfileId = profileId;
                return database.setProfileId(playerId, profileId).thenApply(irrelevant -> finalProfileId);
            }

            setIdInternally(playerId, profileId);
            return CompletableFuture.completedFuture(profileId);
        });
    }

    @Override
    public UUID getCachedProfileId(UUID playerId) {
        return profileMap.get(playerId);
    }

    @Override
    public CompletableFuture<UUID> fetchProfileId(UUID playerId) {
        UUID profileId = getCachedProfileId(playerId);

        if (profileId != null) {
            return CompletableFuture.completedFuture(profileId);
        }

        return database.getProfileId(playerId);
    }

    @Override
    public CompletableFuture<Void> saveProfileId(UUID playerId, UUID profileId) {
        return database.setProfileId(playerId, profileId).thenRun(() -> setIdInternally(playerId, profileId));
    }

    @Override
    public Collection<SkyblockPlayer> getPlayers() {
        return List.copyOf(playerIdMap.values());
    }

    private UUID createProfileId(UUID playerId) {
        return profileMappingFunction.apply(playerId);
    }

    protected void setIdInternally(UUID playerId, UUID profileId) {
        profileMap.put(playerId, profileId);
    }
}
