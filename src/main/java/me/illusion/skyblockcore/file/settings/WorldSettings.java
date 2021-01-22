package me.illusion.skyblockcore.file.settings;

import lombok.Getter;
import me.illusion.skyblockcore.file.path.TargetPath;

@Getter
public class WorldSettings {

    @TargetPath(path = "max-size")
    private int maxSize;
    @TargetPath(path = "inicial-size")
    private int inicialSize;
}
