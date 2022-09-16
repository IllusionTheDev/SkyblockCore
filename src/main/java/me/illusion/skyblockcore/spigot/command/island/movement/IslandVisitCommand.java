package me.illusion.skyblockcore.spigot.command.island.movement;

import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class IslandVisitCommand implements SkyblockCommand {

    private final SkyblockPlugin main;

    public IslandVisitCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public String getIdentifier() {
        return "island.visit.*";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        Player player = (Player) sender;

        String targetPlayer = args[0];

        if (sender.getName().equalsIgnoreCase(targetPlayer)) {
            main.getFiles().getMessages().sendMessage(sender, "commands.island-visit.self");
            return;
        }

        Player onlineTarget = Bukkit.getPlayer(targetPlayer);

        if (onlineTarget != null) {
            Island island = main.getIslandManager().getPlayerIsland(onlineTarget.getUniqueId());

            if (island == null) {
                main.getFiles().getMessages().sendMessage(sender, "commands.island-visit.not-found");
                return;
            }

            island.teleport(player);
            main.getFiles().getMessages().sendMessage(sender, "commands.island-visit.success");
            return;
        }

        CompletableFuture.runAsync(() -> {
            OfflinePlayer target = main.getServer().getOfflinePlayer(targetPlayer);

            UUID targetId = target.getUniqueId();

            IslandData islandData = null;

            try {
                islandData = main.getIslandManager().getIslandData(targetId).get(3, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                ExceptionLogger.log(e);
            }

            if (islandData == null) {
                main.getFiles().getMessages().sendMessage(sender, "commands.island-visit.not-have-island");
                return;
            }

            UUID IslandId = islandData.getId();

            Island loadedIsland = main.getIslandManager().getIsland(IslandId);

            if (loadedIsland == null) {
                main.getFiles().getMessages().sendMessage(sender, "commands.island-visit.island-not-loaded");
                return;
            }
            loadedIsland.teleport(player);
            main.getFiles().getMessages().sendMessage(sender, "commands.island-visit.success");
        }).exceptionally(ex -> {
            ExceptionLogger.log(ex);
            main.getFiles().getMessages().sendMessage(sender, "command.island-visit.error");
            return null;
        });


    }

}
