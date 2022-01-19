package me.illusion.skyblockcore.spigot.command.island.invite;

import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.impl.instance.PacketInvitePlayer;
import me.illusion.skyblockcore.shared.packet.impl.proxy.instance.response.PacketInviteResponse;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
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
                    thr.printStackTrace();
                    return null;
                });

    }

    private CompletableFuture<PacketInviteResponse.Response> sendInvite(Player origin, String targetName) {
        return CompletableFuture.supplyAsync(() -> {
            UUID inviteID = UUID.randomUUID();

            PacketInvitePlayer packet = new PacketInvitePlayer(
                    main.getBungeeMessaging().getServerIdentifier(),
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
