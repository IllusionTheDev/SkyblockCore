package me.illusion.skyblockcore.spigot.listener;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class GeneratorPlaceListener implements Listener {

    private final SkyblockPlugin main;

    public GeneratorPlaceListener(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent e) {
        if (e.getItemInHand() == null)
            return;


    }
}
