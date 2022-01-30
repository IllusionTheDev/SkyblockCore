package me.illusion.skyblockcore.shared.updating;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;


/*
    Heavily modified from
    https://github.com/Stipess1/AutoUpdater/blob/master/src/main/java/com/stipess1/updater/Updater.java

    If there are any copyright issues, or the original author
    seeks to remove this file and its usage from SkyblockCore,
    The author must open an issue on GitHub or file a DMCA takedown.
 */
public class Updater {

    // The folder where update will be downloaded
    private final File updateFolder;
    // The plugin file
    private final File file;
    // ID of a project
    // Direct download link
    private final String downloadLink;

    public Updater(File pluginsFolder, String url, File file) {
        this.updateFolder = pluginsFolder;
        this.file = file;

        downloadLink = url;
        download();
    }

    /**
     * Downloads the file
     */
    private void download() {

        try {
            URL url = new URL(downloadLink);

            try (InputStream in = new BufferedInputStream(url.openStream()); FileOutputStream fout = new FileOutputStream(new File(updateFolder, file.getName()))) {
                final byte[] data = new byte[4096];
                int count;
                while ((count = in.read(data, 0, 4096)) != -1) {
                    fout.write(data, 0, count);
                }
            }

        } catch (Exception exception) {
            System.err.println("[Updater] Error while downloading the file");
            System.err.println("[Updater] Please download the file manually from: " + downloadLink + " and place it in the plugins folder");
        }
    }

}