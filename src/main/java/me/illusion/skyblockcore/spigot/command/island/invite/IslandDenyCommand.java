package me.illusion.skyblockcore.spigot.command.island.invite;

import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketRespondInvite;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketInviteResponse;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import me.illusion.skyblockcore.spigot.data.SkyblockPlayer;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.island.impl.LoadedIsland;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IslandDenyCommand implements SkyblockCommand {

    private final SkyblockPlugin main;

    public IslandDenyCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public String getIdentifier() {
        return "island.deny.*";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        Player player = (Player) sender;
        String targetPlayer = args[0];
        IslandInvite invite = main.getInviteCache().getInvite(player, targetPlayer);

        if (invite == null) {
            main.getFiles().getMessages().sendMessage(sender, "commands.island-deny.no-invite");
            return;
        }

        Island island = main.getIslandManager().getPlayerIsland(invite.getSender());

        if (island == null) {
            main.getFiles().getMessages().sendMessage(sender, "commands.island-deny.not-loaded");
            return;
        }

        PacketRespondInvite packet = new PacketRespondInvite(invite, PacketInviteResponse.Response.INVITE_DENIED);

        main.getPacketManager().send(packet);
        main.getInviteCache().removeInvite(invite);

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
}
