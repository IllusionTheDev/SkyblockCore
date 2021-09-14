package me.illusion.skyblockcore.spigot.command.island;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommand implements SkyblockCommand {

    private final SkyblockPlugin main;

    public IslandCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public String getIdentifier() {
        return "island";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"island.go"};
    }


    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        Player player = (Player) sender;

        Island island = main.getPlayerManager().get(player).getIsland();
        Location center = island.getCenter();

        player.teleport(center);
    }
}
