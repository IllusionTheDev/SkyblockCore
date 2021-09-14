package me.illusion.skyblockcore.spigot.pasting.provider;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class OldFAWEProvider implements FAWEProvider {

    private Constructor<CuboidRegion> constructor;

    @Override
    public void paste(File file, Location location) {
        try {
            Schematic schematic = (Schematic) ClipboardFormats.findByFile(file).load(file);
            schematic.paste(FaweAPI.getWorld(location.getWorld().getName()), new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(File file, Location one, Location two) {
        CuboidRegion region = makeRegion(
                new Vector(one.getX(), one.getY(), one.getZ()),
                new Vector(two.getX(), two.getY(), two.getZ()));

        Schematic schem = new Schematic(region);

        try {
            schem.save(file, ClipboardFormats.findByFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CuboidRegion makeRegion(Vector one, Vector two) {
        if (constructor == null)
            try {
                constructor = CuboidRegion.class.getDeclaredConstructor(Vector.class, Vector.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        try {
            return constructor.newInstance(one, two);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
