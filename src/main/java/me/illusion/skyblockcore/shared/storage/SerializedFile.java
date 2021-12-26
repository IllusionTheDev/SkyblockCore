package me.illusion.skyblockcore.shared.storage;

import lombok.EqualsAndHashCode;

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

    private SerializedFile(File file, byte[] contents) {
        this.file = file;
        this.contents = contents;
    }

    public SerializedFile copy() {
        return new SerializedFile(this.file, contents);
    }

    public static SerializedFile[] loadArray(File[] array) {
        SerializedFile[] newArray = new SerializedFile[array.length];

        for (int i = 0; i < array.length; i++)
            newArray[i] = new SerializedFile(array[i]);

        return newArray;
    }

    public void save() {
        try {
            getFile().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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
                System.out.println("Writing " + contents.length + " bytes to " + file.getAbsolutePath());
                Files.write(file.toPath(), contents);
            } catch (IOException e) {
                e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
