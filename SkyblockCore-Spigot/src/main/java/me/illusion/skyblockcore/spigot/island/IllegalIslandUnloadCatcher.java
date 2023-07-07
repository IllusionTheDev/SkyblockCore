package me.illusion.skyblockcore.spigot.island;

import me.illusion.cosmos.event.session.CosmosUnloadSessionEvent;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IllegalIslandUnloadCatcher implements Listener {

    private final SkyblockSpigotPlugin plugin;

    public IllegalIslandUnloadCatcher(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onUnload(CosmosUnloadSessionEvent event) {
        plugin.getIslandManager().registerRemoved(event.getSession());
    }

}
