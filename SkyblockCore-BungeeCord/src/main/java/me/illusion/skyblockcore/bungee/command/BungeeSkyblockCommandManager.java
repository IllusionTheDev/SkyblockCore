package me.illusion.skyblockcore.bungee.command;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.manager.AbstractSkyblockCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * Represents a BungeeCord implementation of a SkyblockCommandManager.
 */
public class BungeeSkyblockCommandManager extends AbstractSkyblockCommandManager {

    private final SkyblockBungeePlugin platform;

    public BungeeSkyblockCommandManager(SkyblockBungeePlugin platform) {
        super(platform);

        this.platform = platform;
    }

    @Override
    public void registerRoot(String name) {
        ProxyServer proxy = ProxyServer.getInstance();

        proxy.getPluginManager().registerCommand(platform, new BungeeCommand(name));
    }

    @Override
    public void syncCommands() {
        // BungeeCord does not need to sync commands
    }

    private class BungeeCommand extends Command implements TabExecutor {

        public BungeeCommand(String name) {
            super(name);
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            handle(getAudience(sender), getName(), args);
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            return tabComplete(getAudience(sender), getName(), args);
        }

        private SkyblockAudience getAudience(CommandSender sender) {
            if (sender instanceof ProxiedPlayer player) {
                return new BungeeSkyblockPlayerAudience(player.getUniqueId());
            }

            return new BungeeCommandSenderAudience(sender);
        }
    }
}
