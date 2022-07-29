package me.illusion.skyblockcore.spigot.command.island.movement;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.island.impl.LoadedIsland;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IslandExpelCommand implements SkyblockCommand {
    private final SkyblockPlugin main;

    public IslandExpelCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public String getIdentifier() {
        return "island.expel.*";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String getPermission() {
        return "island.expel";
    }

    @Override
    public Map<Integer, List<String>> tabCompleteWildcards() {
        List<String> playerNames = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }

        Map<Integer, List<String>> results = new HashMap<>();

        results.put(1, playerNames);

        return results;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            // todo comms
            return;
        }
        Player player = (Player) sender;

        Island island = main.getIslandManager().getPlayerIsland(player.getUniqueId());

        if (island instanceof LoadedIsland) {
            LoadedIsland loadedIsland = (LoadedIsland) island;
            World world = player.getWorld();
            if (!(world.getPlayers().contains(targetPlayer))){
                main.getFiles().getMessages().sendMessage(player, "island-expel.not-on-island");
                return;
            }

            Island targetPlayerIsland = main.getIslandManager().getPlayerIsland(targetPlayer.getUniqueId());
            targetPlayerIsland.teleport(targetPlayer);

        }
        // Check if the target is on the current instance

    }


}

