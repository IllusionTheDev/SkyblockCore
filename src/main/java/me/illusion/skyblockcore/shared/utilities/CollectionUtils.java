package me.illusion.skyblockcore.shared.utilities;

import me.illusion.skyblockcore.shared.storage.SerializedFile;

import java.io.File;
import java.util.Collection;

public class CollectionUtils {

    public static <T> T[] arrayOf(Collection<T> collection) {
        return (T[]) collection.toArray();
    }

    public static SerializedFile[] serializeFiles(File... files) {
        return SerializedFile.loadArray(files);
    }

}
