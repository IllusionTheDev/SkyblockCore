package me.illusion.skyblockcore.listener;

import me.illusion.skyblockcore.CorePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class GeneratorPlaceListener implements Listener {

    private final CorePlugin main;

    public GeneratorPlaceListener(CorePlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent e) {
        if (e.getItemInHand() == null)
            return;


    }
}
