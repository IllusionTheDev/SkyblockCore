package me.illusion.skyblockcore.spigot.island.slime.wrapper;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import java.util.concurrent.CompletableFuture;

public interface SlimeAPI {

    SlimePlugin getPlugin();

    SlimeLoader getPreferredLoader();

    SlimeLoader getLoader(String name);

    CompletableFuture<SlimeWorld> loadWorld(String name);

    CompletableFuture<SlimeWorld> copyWorld(SlimeWorld world, String newName);

    CompletableFuture<Void> unloadWorld(String name);

    CompletableFuture<Void> saveWorld(String name);

    CompletableFuture<Void> deleteWorld(String name);

    CompletableFuture<SlimeWorld> getOrLoadWorld(String name);

    default CompletableFuture<SlimeWorld> getOrLoadOrCopyWorld(String name, String copyName) {
        return getOrLoadWorld(name).thenCompose(world -> {
            if (world == null) {
                return getOrLoadWorld(copyName).thenCompose(copy -> {
                    if (copy == null) {
                        return CompletableFuture.completedFuture(null);
                    }

                    return copyWorld(copy, name);
                });
            }

            return CompletableFuture.completedFuture(world);
        });
    }
}
