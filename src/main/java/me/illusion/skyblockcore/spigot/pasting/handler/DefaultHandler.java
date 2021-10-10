package me.illusion.skyblockcore.spigot.pasting.handler;

import com.google.common.io.Files;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class DefaultHandler implements PastingHandler {

    private final SkyblockPlugin main;
    private String extension;

    public DefaultHandler(SkyblockPlugin main) {
        this.main = main;
    }

    private void paste(SerializedFile serializedFile, Location loc) {
        File file = null;
        try {
            file = serializedFile.getFile().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (extension == null)
            extension = getExtension(file.getName());

        World world = loc.getWorld();

        String name = world.getName();

        // Obtain the region folder for the world
        File regionFolder = new File(world.getWorldFolder() + File.separator + "region");

        // Unload the world, to not cause issues
        Bukkit.unloadWorld(world, false);

        regionFolder.delete();
        regionFolder.mkdir();

        // Create the new file
        File newFile = new File(regionFolder, file.getName());

        try {
            // Create the region file
            newFile.createNewFile();

            // Copy the file
            Files.copy(file, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Re-load world
        new WorldCreator(name).createWorld();
    }

    @Override
    public void paste(SerializedFile[] file, Location loc) {
        for (SerializedFile f : file)
            paste(f, loc);
    }

    @Override
    public void save(Island island, Consumer<SerializedFile[]> action) {
        World world = Bukkit.getWorld(island.getWorld());
        world.save();

        main.getWorldManager().whenNextSave(($) -> {
            File regionFolder = new File(world.getWorldFolder() + File.separator + "region");

            List<SerializedFile> list = new ArrayList<>();

            Location one = island.getPointOne();
            Location two = island.getPointTwo();

            int xOne = one.getBlockX() >> 9;
            int zOne = one.getBlockZ() >> 9;
            int xTwo = two.getBlockX() >> 9;
            int zTwo = two.getBlockZ() >> 9;

            for (int x = xOne; x <= xTwo; x++)
                for (int z = zOne; z <= zTwo; z++)
                    list.add(new SerializedFile(new File(regionFolder, "r." + x + "." + z + "." + extension)));

            action.accept(list.toArray(new SerializedFile[]{}));
        }, world.getName());

    }

    @Override
    public PastingType getType() {
        return PastingType.DEFAULT;
    }

    private String getExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return filename.substring(index + 1);
    }
}
