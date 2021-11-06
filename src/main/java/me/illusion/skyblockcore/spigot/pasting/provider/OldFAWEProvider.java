package me.illusion.skyblockcore.spigot.pasting.provider;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OldFAWEProvider implements FAWEProvider {

    private Constructor<CuboidRegion> constructor;
    private Method formatMethod;
    private Method loadMethod;

    @Override
    public void paste(File file, Location location) {
        Schematic schematic = loadSchematic(file);
        schematic.paste(FaweAPI.getWorld(location.getWorld().getName()), new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    @Override
    public void save(File file, Location one, Location two) {
        CuboidRegion region = makeRegion(
                new Vector(one.getX(), one.getY(), one.getZ()),
                new Vector(two.getX(), two.getY(), two.getZ()));

        Schematic schem = new Schematic(region);

        try {
            schem.save(file, (ClipboardFormat) getFormat(file));
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

    private Schematic loadSchematic(File file) {

        try {
            Object format = getFormat(file);
            return (Schematic) loadMethod.invoke(format, file);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Object getFormat(File file) {
        if (formatMethod == null) {
            Class<?> clazz = null;

            try {
                clazz = Class.forName("com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                formatMethod = clazz.getDeclaredMethod("findByFile", File.class);
                loadMethod = clazz.getDeclaredMethod("load", File.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        try {
            return formatMethod.invoke(null, file);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;

    }
}
