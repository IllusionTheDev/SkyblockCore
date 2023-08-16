package me.illusion.skyblockcore.bungee.command;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SimpleSkyblockCommand extends Command { // TODO: Write a platform-independent command system

    private final SkyblockProxyPlatform platform;

    public SimpleSkyblockCommand(SkyblockProxyPlatform platform) {
        super("play-skyblock");

        this.platform = platform;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            return;
        }

        fetchIslandId(player.getUniqueId()).thenCompose(platform.getMatchmaker()::matchMake).thenAccept(server -> {
            if (server == null) {
                player.sendMessage("Could not find a server to connect to.");
                return;
            }

            player.connect(ProxyServer.getInstance().getServerInfo(server));
        });
    }

    private CompletableFuture<UUID> fetchIslandId(UUID playerId) {
        return platform.getDatabaseRegistry().getChosenDatabase().getProfileId(playerId).thenCompose(uuid -> {
            if (uuid == null) {
                return CompletableFuture.completedFuture(null);
            } else {
                return platform.getDatabaseRegistry().getChosenDatabase().fetchIslandId(uuid);
            }
        });
    }
}
