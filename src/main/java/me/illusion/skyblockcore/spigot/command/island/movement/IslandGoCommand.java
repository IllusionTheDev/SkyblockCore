package me.illusion.skyblockcore.spigot.command.island.movement;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandGoCommand implements SkyblockCommand {

    private final SkyblockPlugin main;
    private final String identifier;

    public IslandGoCommand(SkyblockPlugin main, String identifier) {
        this.main = main;
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        Player player = (Player) sender;

        Island island = main.getPlayerManager().get(player).getIsland();
        island.teleport(player);
        main.getMessages().sendMessage(sender, "command.island-teleport");
    }
}
