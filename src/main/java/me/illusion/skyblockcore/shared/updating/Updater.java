package me.illusion.skyblockcore.shared.updating;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;


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

            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
                URLConnection conn = url.openConnection();

                try (InputStream in = conn.getInputStream()) {
                    byte[] buffer = new byte[1024];

                    int numRead;
                    while ((numRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, numRead);
                    }
                }
            }

        } catch (Exception exception) {
            System.err.println("[Updater] Error while downloading the file");
            System.err.println("[Updater] Please download the file manually from: " + downloadLink + " and place it in the plugins folder");
        }
    }

}