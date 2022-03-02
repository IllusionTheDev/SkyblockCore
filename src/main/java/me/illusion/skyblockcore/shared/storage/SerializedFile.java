package me.illusion.skyblockcore.shared.storage;

import lombok.EqualsAndHashCode;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@EqualsAndHashCode
public class SerializedFile implements Serializable {

    private File file;
    private byte[] contents;

    public SerializedFile(File file) {
        setFile(file);
    }

    public SerializedFile(File file, byte[] contents) {
        this.file = file;
        this.contents = contents;
    }

    public SerializedFile copy() {
        return new SerializedFile(this.file, contents);
    }

    public static SerializedFile[] loadArray(File... array) {
        SerializedFile[] newArray = new SerializedFile[array.length];

        for (int index = 0; index < array.length; index++)
            newArray[index] = new SerializedFile(array[index]);

        return newArray;
    }

    public void save() {
        try {
            getFile().get();
        } catch (InterruptedException | ExecutionException e) {
            ExceptionLogger.log(e);
        }
    }

    public CompletableFuture<File> getFile() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }

                Files.write(file.toPath(), contents);
            } catch (IOException e) {
                ExceptionLogger.log(e);
            }
            return file;
        });
    }

    public final void setFile(File file) {
        setFile(file, true);
    }

    public final void setFile(File file, boolean readNewContents) {
        this.file = file;

        if (!file.exists() || !readNewContents)
            return;

        try {
            this.contents = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            ExceptionLogger.log(e);
        }
    }

    public byte[] getBytes() {
        return contents;
    }

    public File getCachedFile() {
        return file;
    }
}
