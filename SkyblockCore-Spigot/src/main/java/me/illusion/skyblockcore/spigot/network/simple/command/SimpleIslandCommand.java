package me.illusion.skyblockcore.spigot.network.simple.command;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.command.command.impl.ExecutionContext;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This is the simple /island command, which immediately teleports the player to its island.
 */
public class SimpleIslandCommand extends AdvancedCommand {

    private final MessagesFile messages;
    private final SkyblockIslandManager skyblockIslandManager;

    public SimpleIslandCommand(SimpleSkyblockNetwork network) {
        super("island");

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

        if (island == null) {
            messages.sendMessage(player, "no-island-loaded");
            return;
        }

        player.teleport(SkyblockBukkitAdapter.toBukkitLocation(island.getCenter()));
    }
}
