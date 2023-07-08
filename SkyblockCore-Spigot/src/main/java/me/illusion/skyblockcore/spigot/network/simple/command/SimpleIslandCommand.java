package me.illusion.skyblockcore.spigot.network.simple.command;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.command.command.impl.ExecutionContext;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SimpleIslandCommand extends AdvancedCommand {

    private final MessagesFile messages;
    private final IslandManager islandManager;

    public SimpleIslandCommand(SimpleSkyblockNetwork network) {
        super("island");

        messages = network.getMessages();
        islandManager = network.getIslandManager();
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSender sender, ExecutionContext context) {
        Player player = (Player) sender;
        Island island = islandManager.getPlayerIsland(player);

        if (island == null) {
            messages.sendMessage(player, "no-island-loaded");
            return;
        }

        player.teleport(island.getCenter());
    }
}
