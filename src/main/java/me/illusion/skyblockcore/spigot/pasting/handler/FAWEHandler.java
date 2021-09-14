package me.illusion.skyblockcore.spigot.pasting.handler;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.data.SerializedFile;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static me.illusion.skyblockcore.spigot.pasting.PastingType.FAWE;

public class FAWEHandler implements PastingHandler {

    private final SkyblockPlugin main;

    public FAWEHandler(SkyblockPlugin main) {
        this.main = main;
    }

    private void paste(SerializedFile serializedFile, Location loc) {
        File file = null;
        try {
            file = serializedFile.getFile().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            Schematic schem = ClipboardFormats.findByFile(file).load(file);
            schem.paste(FaweAPI.getWorld(loc.getWorld().getName()), new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paste(SerializedFile[] file, Location loc) {
        for (SerializedFile f : file)
            paste(f, loc);
    }

    @Override
    public void save(Island island, Consumer<SerializedFile[]> action) {
        File file = new File(main.getDataFolder() + File.separator + "cache", island.getData().getId() + ".schematic");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Location p1 = island.getPointOne();
        Location p2 = island.getPointTwo();

        CuboidRegion region = new CuboidRegion(
                new Vector(p1.getX(), p1.getY(), p1.getZ()),
                new Vector(p2.getX(), p2.getY(), p2.getZ()));

        Schematic schem = new Schematic(region);

        try {
            schem.save(file, ClipboardFormats.findByFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        action.accept(array(new SerializedFile(file)));
    }

    @SafeVarargs
    private final <T> T[] array(T... types) {
        return types;
    }

    @Override
    public PastingType getType() {
        return FAWE;
    }
}
