package me.illusion.skyblockcore.spigot.command.island.invite;

import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.PacketManager;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketInvitePlayer;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketInviteResponse;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        sendInvite(player, target.getName())
                .thenAccept(response -> {
                    player.sendMessage("Response: " + response);
                })
                .exceptionally((thr) -> {
                    ExceptionLogger.log(thr);
                    return null;
                });

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

    private CompletableFuture<PacketInviteResponse.Response> sendInvite(Player origin, String targetName) {
        return CompletableFuture.supplyAsync(() -> {
            UUID inviteID = UUID.randomUUID();

            PacketInvitePlayer packet = new PacketInvitePlayer(
                    PacketManager.getServerIdentifier(),
                    new IslandInvite(inviteID, origin.getUniqueId(), targetName));

            main.getPacketManager().send(packet);

            PacketInviteResponse response = main.getPacketManager()
                    .await(PacketInviteResponse.class,
                            (invite) -> invite.getInvite().getInviteId().equals(inviteID),
                            5);

            if (response == null)
                return PacketInviteResponse.Response.RESPONSE_NOT_FOUND;

            return response.getResponse();
        });
    }
}
