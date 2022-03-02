package me.illusion.skyblockcore.spigot.pasting.provider;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
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
            ExceptionLogger.log(e);
        }
    }

    @Override
    public void save(File file, Location one, Location two) {
        CuboidRegion region = new CuboidRegion(
                BlockVector3.at(one.getX(), one.getY(), one.getZ()),
                BlockVector3.at(two.getX(), two.getY(), two.getZ()));

        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try {
            clipboard.save(file, ClipboardFormats.findByFile(file));
        } catch (IOException e) {
            ExceptionLogger.log(e);
        }
    }
}
