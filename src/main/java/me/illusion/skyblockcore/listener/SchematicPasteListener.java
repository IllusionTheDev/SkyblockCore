package me.illusion.skyblockcore.listener;

import com.sk89q.worldedit.event.extent.PasteEvent;
import me.illusion.skyblockcore.island.generator.OreGenerator;
import me.illusion.skyblockcore.island.generator.OreGeneratorType;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.util.stream.Stream;

public class SchematicPasteListener implements Listener {

    @EventHandler
    private void onPaste(PasteEvent e) {
        e.getClipboard()
                .getRegion()
                .getChunks()
                .stream()
                .map((c) ->
                        Bukkit.getWorld(
                                e.getPlayer()
                                        .getWorld()
                                        .getName())
                                .getChunkAt(c.getBlockX(), c.getBlockZ()))
                .forEach(c ->
                        Stream.of(c.getTileEntities())
                                .filter(state -> state instanceof Sign)
                                .map(state -> (Sign) state)
                                .forEach(sign -> {
                                    boolean generator = false;
                                    int type = 0;
                                    boolean generated = false;

                                    for (int i = 0; i < 4; i++) {
                                        String line = sign.getLine(i);

                                        if (line == null)
                                            return;

                                        if (i == 0 && line.equals("§aGenerator"))
                                            generator = true;

                                        if (!generator)
                                            return;

                                        if (i == 1)
                                            type = Integer.parseInt(line);
                                        if (i == 2)
                                            generated = Boolean.parseBoolean(line);
                                    }

                                    OreGeneratorType genType = OreGeneratorType.fromId(type);

                                    if (genType == null)
                                        return;

                                    OreGenerator oreGen = new OreGenerator(null, null, genType, generated ? 0 : Instant.now().getEpochSecond() + genType.getCooldownSeconds());
                                }));

    }

}
