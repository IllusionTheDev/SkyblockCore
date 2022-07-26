package me.illusion.skyblockcore.spigot.command.island.invite;

import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketInvitePlayer;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class IslandInviteCommand implements SkyblockCommand {

    private final SkyblockPlugin main;

    public IslandInviteCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public String getIdentifier() {
        return "island.invite.*";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        String targetPlayer = args[0];
        Player player = (Player) sender;

        // Check if the target is on the current instance
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetPlayer);

        sendInvite(player, target.getUniqueId(), targetPlayer);

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

    private void sendInvite(Player origin, UUID targetId, String targetName) {
        CompletableFuture.runAsync(() -> {
            UUID inviteId = UUID.randomUUID();
            String playerName = origin.getName();

            IslandInvite invite = new IslandInvite(inviteId, origin.getUniqueId(), playerName, targetId, targetName, Instant.now().getEpochSecond() + 60);

            main.getInviteCache().addInvite(invite);

            PacketInvitePlayer packet = new PacketInvitePlayer(invite);

            main.getPacketManager().send(packet);
        });
    }
}
