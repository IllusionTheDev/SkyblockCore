package me.illusion.skyblockcore.server.network.complex.command;

import me.illusion.skyblockcore.common.command.manager.SkyblockCommandManager;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * Represents a command that allows players to teleport to their island. This will fetch the server where the island is loaded, and send a request to teleport
 * the player to that server. If the island is cached locally, the player will be teleported to the island directly.
 */
public class ComplexIslandCommand {

    public ComplexIslandCommand(ComplexSkyblockNetwork network) {
        SkyblockCommandManager<?> commandManager = network.getPlatform().getCommandManager();
        SkyblockIslandManager islandManager = network.getIslandManager();
        SkyblockMessagesFile messages = network.getMessages();

        commandManager.newCommand("island")
            .audience(SkyblockPlayer.class)
            .handler((player, context) -> {
                SkyblockIsland island = islandManager.getPlayerIsland(player.getUniqueId());

                if (island != null) {
                    player.teleport(island.getCenter());
                    return;
                }

                network.getDatabase().fetchIslandId(player.getUniqueId()).thenAccept(islandId -> {
                    if (islandId == null) {
                        messages.sendMessage(player, "no-island-loaded");
                        return;
                    }

                    network.getCommunicationsHandler().attemptTeleportToIsland(player, islandId).thenAccept(success -> {
                        if (Boolean.FALSE.equals(success)) {
                            messages.sendMessage(player, "no-island-loaded");
                        }

                    });
                });
            })
            .build();
    }
}
