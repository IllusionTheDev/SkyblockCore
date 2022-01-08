package me.illusion.skyblockcore.spigot.island;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.utilities.WorldUtils;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Getter
public class Island {

    private final SkyblockPlugin main;

    private final Location pointOne;
    private final Location pointTwo;
    private final Location center;

    private final IslandData data;

    private final String world;

    public Island(SkyblockPlugin main, Location pointOne, Location pointTwo, Location center, IslandData data, String world) {
        this.main = main;
        this.pointOne = pointOne;
        this.pointTwo = pointTwo;
        this.center = center;
        this.data = data;
        this.world = world;

        main.getIslandManager().register(this);
    }

    /**
     * Saves the island
     */
    public void save(Runnable afterSave) {
        main.getPastingHandler().save(this, schem -> {
            System.out.println("saved island");
            System.out.println("Saved schematic names: ");

            for (SerializedFile file : schem) {
                // Check for similarity
                byte[] size = file.getBytes();
                File cached = file.getCachedFile();

                if (cached.exists()) {
                    try {
                        byte[] cachedSize = Files.readAllBytes(cached.toPath());

                        if (cachedSize.length == size.length && Arrays.equals(cachedSize, size)) {
                            System.out.println("Files are equal yay");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            data.setIslandSchematic(schem);

            CompletableFuture.runAsync(this::saveData)
                    .thenRun(afterSave);
        });
    }

    /**
     * Cleans the island, by regenerating its chunks
     */
    public void cleanIsland() {
        System.out.println("Cleaning island...");

        WorldUtils
                .unload(main, world)
                .thenRun(() -> {
                    WorldUtils.deleteRegionFolder(main, world);

                    main.getIslandManager().unregister(this);
                    main.getWorldManager().unregister(this.world);
                });


    }

    /**
     * Saves Island data
     */
    private void saveData() {
        main.getStorageHandler().save(data.getId(), data, "ISLAND");
    }

}
