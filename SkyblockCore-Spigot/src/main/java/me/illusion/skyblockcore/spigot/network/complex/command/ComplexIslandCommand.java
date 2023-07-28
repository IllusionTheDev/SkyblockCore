package me.illusion.skyblockcore.spigot.network.complex.command;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.command.command.impl.ExecutionContext;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a command that allows players to teleport to their island. This will fetch the server where the island is loaded, and send a request to teleport
 * the player to that server. If the island is cached locally, the player will be teleported to the island directly.
 */
public class ComplexIslandCommand extends AdvancedCommand {

    private final ComplexSkyblockNetwork network;

    private final MessagesFile messages;
    private final SkyblockIslandManager skyblockIslandManager;

    public ComplexIslandCommand(ComplexSkyblockNetwork network) {
        super("island");

        this.network = network;

        messages = network.getMessages();
        skyblockIslandManager = network.getIslandManager();
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSender sender, ExecutionContext context) {
        Player player = (Player) sender;
        SkyblockIsland island = skyblockIslandManager.getPlayerIsland(player.getUniqueId());

        if (island != null) {
            player.teleport(SkyblockBukkitAdapter.toBukkitLocation(island.getCenter()));
            return;
        }

        network.getDatabase().fetchIslandId(player.getUniqueId()).thenAccept(islandId -> {
            if (islandId == null) {
                messages.sendMessage(player, "no-island-loaded");
                return;
            }

            network.getCommunicationsHandler().attemptTeleportToIsland(player, islandId).thenAccept(success -> {
                if (!success) {
                    messages.sendMessage(player, "no-island-loaded");
                }

            });
        });

    }
}
