package me.illusion.skyblockcore.bungee.command;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.bungee.data.PlayerFinder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SkyblockCommand extends Command {

    private final SkyblockBungeePlugin main;

    public SkyblockCommand(SkyblockBungeePlugin main) {
        super("skyblock");
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            //message
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        PlayerFinder playerFinder = main.getPlayerFinder();

        playerFinder.requestIslandServer(player.getUniqueId()).whenComplete((serverName, thr) -> {
            if (serverName == null) // Assign available server if no members are online
                serverName = playerFinder.getAvailableServer();

            if (serverName == null) // If no space found
                return;

            ServerInfo targetServer = main.getProxy().getServerInfo(serverName);

            player.connect(targetServer);
        });
    }
}
