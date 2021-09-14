package me.illusion.skyblockcore.spigot.pasting.provider;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;

public class NewFAWEProvider implements FAWEProvider {

    @Override
    public void paste(File file, Location location) {
        try {
            Clipboard clipboard = ClipboardFormats.findByFile(file).load(file);
            clipboard.paste(FaweAPI.getWorld(location.getWorld().getName()), BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(File file, Location one, Location two) {
        CuboidRegion region = new CuboidRegion(
                BlockVector3.at(one.getX(), one.getY(), one.getZ()),
                BlockVector3.at(two.getX(), two.getY(), two.getZ()));

        Schematic schem = new Schematic(region);

        try {
            schem.save(file, ClipboardFormats.findByFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
