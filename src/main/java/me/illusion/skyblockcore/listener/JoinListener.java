package me.illusion.skyblockcore.listener;

import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.data.SkyblockPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final CorePlugin main;

    public JoinListener(CorePlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        SkyblockPlayer player = new SkyblockPlayer(main, e.getPlayer().getUniqueId());

    }
}
