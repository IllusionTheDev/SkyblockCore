package me.illusion.skyblockcore.pasting.handler;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import me.illusion.skyblockcore.island.Island;
import me.illusion.skyblockcore.pasting.PastingHandler;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;

public class FAWEHandler implements PastingHandler {

    private void paste(File file, Location loc) {
        try {
            Schematic schem = ClipboardFormat.SCHEMATIC.load(file);
            schem.paste(FaweAPI.getWorld(loc.getWorld().getName()), new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paste(File[] file, Location loc) {
        for (File f : file)
            paste(file, loc);
    }

    @Override
    public File[] save(Island island) {
        return new File[0];
    }
}
