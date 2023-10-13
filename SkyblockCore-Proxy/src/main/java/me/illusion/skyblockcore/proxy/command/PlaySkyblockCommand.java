package me.illusion.skyblockcore.proxy.command;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.manager.SkyblockCommandManager;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.common.storage.island.SkyblockIslandStorage;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;
import me.illusion.skyblockcore.proxy.audience.SkyblockProxyPlayerAudience;

/**
 * Represents a simple play-skyblock command. This will attempt to matchmake the player and connect them to the server. If no server is found, the player will
 * be notified.
 */
public class PlaySkyblockCommand {

    private final SkyblockProxyPlatform platform;

    public PlaySkyblockCommand(SkyblockProxyPlatform platform) {
        this.platform = platform;

        SkyblockCommandManager<SkyblockAudience> commandManager = platform.getCommandManager();
        SkyblockMessagesFile messages = platform.getMessagesFile();

        commandManager.newCommand("play-skyblock")
            .audience(SkyblockProxyPlayerAudience.class)
            .handler((player, context) -> {
                UUID playerId = player.getUniqueId();

                fetchIslandId(playerId).thenCompose(platform.getMatchmaker()::matchMake).thenAccept(server -> {
                    if (server == null) {
                        messages.sendMessage(player, "no-server-found");
                        return;
                    }

                    player.connect(server);
                });
            })
            .build();

    }

    private CompletableFuture<UUID> fetchIslandId(UUID playerId) {
        SkyblockIslandStorage database = platform.getDatabaseRegistry().getStorage(SkyblockProfileStorage.class);

        return database.getProfileId(playerId).thenCompose(uuid -> {
            if (uuid == null) {
                return CompletableFuture.completedFuture(null);
            }

            return database.fetchIslandId(uuid);
        });
    }

}
