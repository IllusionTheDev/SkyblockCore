package me.illusion.skyblockcore.spigot.pasting.handler;

import com.google.common.io.Files;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DefaultHandler implements PastingHandler {

    private final SkyblockPlugin main;

    public DefaultHandler(SkyblockPlugin main) {
        this.main = main;
    }

    private String extension;

    private void paste(File file, Location loc) {

        if (extension == null)
            extension = FilenameUtils.getExtension(file.getAbsolutePath());

        System.out.println(extension);

        World world = loc.getWorld();

        String name = world.getName();

        // Divide blockX by 512, also known as chunkX/16
        int x = loc.getBlockX() >> 9;
        // Divide blockZ by 512, also known as chunkZ/16
        int z = loc.getBlockZ() >> 9;

        // Obtain the region folder for the world
        File regionFolder = new File(world.getWorldFolder() + File.separator + "region");

        regionFolder.delete();
        regionFolder.mkdir();

        // Unload the world, to not cause issues
        Bukkit.unloadWorld(world, false);

        // Create the new file
        File newFile = new File(regionFolder, "r." + x + "." + z + "." + extension);

        try {
            // Create the region file, if not exists
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
    public void paste(File[] file, Location loc) {
        for (File f : file)
            paste(f, loc);
    }

    @Override
    public void save(Island island, Consumer<File[]> action) {
        World world = Bukkit.getWorld(island.getWorld());
        world.save();

        main.getWorldManager().whenNextSave(($) -> {
            File regionFolder = new File(world.getWorldFolder() + File.separator + "region");

            List<File> list = new ArrayList<>();

            Location one = island.getPointOne();
            Location two = island.getPointTwo();

            int xOne = one.getBlockX() >> 9;
            int zOne = one.getBlockZ() >> 9;
            int xTwo = two.getBlockX() >> 9;
            int zTwo = two.getBlockZ() >> 9;

            for (int x = xOne; x <= xTwo; x++)
                for (int z = zOne; z <= zTwo; z++)
                    list.add(new File(regionFolder, "r." + x + "." + z + "." + extension));

            action.accept(list.toArray(new File[]{}));
        }, world.getName());

    }

    @Override
    public PastingType getType() {
        return PastingType.DEFAULT;
    }
}
