package me.illusion.skyblockcore.shared.dependency;

import me.illusion.skyblockcore.shared.updating.Updater;

import java.io.File;

public class DependencyDownloader {

    private final File pluginsFolder;
    private Runnable downloadAction;

    public DependencyDownloader(File pluginsFolder) {
        this.pluginsFolder = pluginsFolder;
    }

    public void onDownload(Runnable downloadAction) {
        this.downloadAction = downloadAction;
    }

    public void dependOn(String className, String url, String fileName) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            new Updater(pluginsFolder, url, createFile(fileName));
            downloadAction.run();
        }
    }

    private File createFile(String fileName) {
        File file = new File(pluginsFolder, fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
