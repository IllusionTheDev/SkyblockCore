package me.illusion.skyblockcore.command.island;

import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.command.SkyblockCommand;
import me.illusion.skyblockcore.island.Island;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommand implements SkyblockCommand {

    private final CorePlugin main;

    public IslandCommand(CorePlugin main) {
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
