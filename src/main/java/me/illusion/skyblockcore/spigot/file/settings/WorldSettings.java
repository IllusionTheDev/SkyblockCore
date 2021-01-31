package me.illusion.skyblockcore.spigot.file.settings;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.file.path.TargetPath;

@Getter
public class WorldSettings {

    @TargetPath(path = "max-size")
    private int maxSize;
    @TargetPath(path = "inicial-size")
    private int inicialSize;
}
