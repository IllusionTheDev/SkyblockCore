package me.illusion.skyblockcore.spigot.pasting.handler;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.grinderwolf.swm.plugin.loaders.file.FileLoader;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.island.impl.LoadedIsland;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SlimeHandler implements PastingHandler {

    private final Field worldFilesField;
    private final Field worldFolderField;

    public SlimeHandler() {
        Field worldFilesField1;
        Field worldFolderField1;
        try {
            worldFilesField1 = FileLoader.class.getDeclaredField("worldFiles");
            worldFilesField1.setAccessible(true);

            worldFolderField1 = FileLoader.class.getDeclaredField("worldDir");
            worldFolderField1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            worldFilesField1 = null;
            worldFolderField1 = null;
        }
        worldFilesField = worldFilesField1;
        worldFolderField = worldFolderField1;

    }

    @Override
    public CompletableFuture<Void> paste(SerializedFile[] file, String worldName, Vector point) {
        return CompletableFuture.runAsync(() -> {
            SWMPlugin plugin = SWMPlugin.getInstance();
            FileLoader fileLoader = (FileLoader) plugin.getLoader("file");

            try {
                File worldFolder = (File) worldFolderField.get(fileLoader);
                File worldFile = new File(worldFolder, worldName + ".slime");

                SerializedFile first = file[0];
                first.setFile(worldFile, false);

                fileLoader.loadWorld(worldName, false);
            } catch (IllegalAccessException | WorldInUseException | UnknownWorldException | IOException e) {
                ExceptionLogger.log(e);
            }

        });
    }

    @Override
    public CompletableFuture<Void> save(LoadedIsland island, Consumer<SerializedFile[]> action) {
        SWMPlugin plugin = SWMPlugin.getInstance();
        World world = Bukkit.getWorld(island.getWorld());

        SlimeWorld slimeWorld = plugin.getNms().getSlimeWorld(world);
        CraftSlimeWorld craftSlimeWorld = (CraftSlimeWorld) slimeWorld;


        try {
            FileLoader fileLoader = (FileLoader) plugin.getLoader("file");

            Map<String, RandomAccessFile> files = (Map<String, RandomAccessFile>) worldFilesField.get(fileLoader);
            RandomAccessFile file = files.get(world.getName());

            if (file == null) {
                return CompletableFuture.completedFuture(null);
            }

            fileLoader.saveWorld(world.getName(), craftSlimeWorld.serialize(), true);

            // convert randomaccessfile to file
            File worldFolder = (File) worldFolderField.get(fileLoader);
            File worldFile = new File(worldFolder, world.getName() + ".slime");
            action.accept(SerializedFile.loadArray(worldFile));
        } catch (Exception e) {
            ExceptionLogger.log(e);
        }

        return CompletableFuture.completedFuture(null);

    }

    @Override
    public PastingType getType() {
        return PastingType.SLIME;
    }

    @Override
    public boolean requiresLoadedWorld() {
        return false;
    }
}
