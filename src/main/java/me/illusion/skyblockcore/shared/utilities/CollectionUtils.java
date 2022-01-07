package me.illusion.skyblockcore.shared.utilities;

import me.illusion.skyblockcore.shared.storage.SerializedFile;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CollectionUtils {

    public static <T> T[] arrayOf(Collection<T> collection) {
        return (T[]) collection.toArray();
    }

    public static CompletableFuture<Void> allOf(List<CompletableFuture<?>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public static SerializedFile[] serializeFiles(File... files) {
        return SerializedFile.loadArray(files);
    }

}
