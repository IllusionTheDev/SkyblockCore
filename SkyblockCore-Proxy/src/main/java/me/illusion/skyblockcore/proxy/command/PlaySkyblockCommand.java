package me.illusion.skyblockcore.proxy.command;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.manager.SkyblockCommandManager;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;
import me.illusion.skyblockcore.proxy.audience.SkyblockProxyPlayerAudience;

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
        SkyblockFetchingDatabase database = platform.getDatabaseRegistry().getChosenDatabase();

        return database.getProfileId(playerId).thenCompose(uuid -> {
            if (uuid == null) {
                return CompletableFuture.completedFuture(null);
            }

            return database.fetchIslandId(uuid);
        });
    }

}
