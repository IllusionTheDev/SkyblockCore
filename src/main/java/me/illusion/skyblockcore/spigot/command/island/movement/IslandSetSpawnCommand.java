package me.illusion.skyblockcore.spigot.command.island.movement;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.island.impl.LoadedIsland;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandSetSpawnCommand implements SkyblockCommand {
    private final SkyblockPlugin main;

    public IslandSetSpawnCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public String getIdentifier() {
        return "island.setspawn";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        Player player = (Player) sender;

        Island island = main.getPlayerManager().get(player).getIsland();

        if (!(island instanceof LoadedIsland)) {
            main.getFiles().getMessages().sendMessage(player, "command.island.setspawn.invalid-server");
            return;
        }

        LoadedIsland loadedIsland = (LoadedIsland) island;
        loadedIsland.setSpawnPoint(player.getLocation());

        main.getFiles().getMessages().sendMessage(sender, "command.island-set-spawn.success");
    }
}
