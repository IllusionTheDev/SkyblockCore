package me.illusion.skyblockcore.file.settings;

import lombok.Getter;
import me.illusion.skyblockcore.file.path.TargetPath;
import org.bukkit.Bukkit;
import org.bukkit.World;

@Getter
public class WorldSettings {

    @TargetPath
    private int distance;
    @TargetPath
    private String world;
    @TargetPath(path = "max-size")
    private int maxSize;
    @TargetPath(path = "inicial-size")
    private int inicialSize;

    public World getBukkitWorld() {
        return Bukkit.getWorld(world);
    }
}
