package me.illusion.skyblockcore.spigot.data;

import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

@EqualsAndHashCode
public class SerializedFile implements Serializable {

    private File file;
    private byte[] contents;

    public SerializedFile(File file) {
        setFile(file);
    }

    public static SerializedFile[] loadArray(File[] array) {
        SerializedFile[] newArray = new SerializedFile[array.length];

        for (int i = 0; i < array.length; i++)
            newArray[i] = new SerializedFile(array[i]);

        return newArray;
    }

    public CompletableFuture<File> getFile() {
        return CompletableFuture.supplyAsync(() -> {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Files.write(file.toPath(), contents);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        });
    }

    public final void setFile(File file) {
        this.file = file;
        try {
            this.contents = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
