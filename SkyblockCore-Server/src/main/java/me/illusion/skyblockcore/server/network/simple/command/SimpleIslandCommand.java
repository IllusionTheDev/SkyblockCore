package me.illusion.skyblockcore.server.network.simple.command;

import me.illusion.skyblockcore.common.command.manager.SkyblockCommandManager;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.simple.SimpleSkyblockNetwork;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * This is the simple /island command, which immediately teleports the player to its island.
 */
public class SimpleIslandCommand {

    public SimpleIslandCommand(SimpleSkyblockNetwork network) {
        SkyblockCommandManager<?> commandManager = network.getCommandManager();
        SkyblockIslandManager islandManager = network.getIslandManager();
        SkyblockMessagesFile messages = network.getMessages();

        commandManager.newCommand("island")
            .audience(SkyblockPlayer.class)
            .handler((player, context) -> {
                SkyblockIsland island = islandManager.getPlayerIsland(player.getUniqueId());

                if (island == null) {
                    messages.sendMessage(player, "no-island-loaded");
                    return;
                }

                player.teleport(island.getCenter());
            })
            .build();
    }
}
