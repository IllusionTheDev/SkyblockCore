package me.illusion.skyblockcore.shared.utilities;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Objects;

public class ExceptionLogger {

    private static File folder;
    private static int exceptionCount = 0;

    public static void setFolder(File folder) {
        folder.mkdir();

        ExceptionLogger.folder = folder;
        ExceptionLogger.exceptionCount = Objects.requireNonNull(folder.listFiles()).length;
    }

    @SneakyThrows
    public static void log(final Throwable exception) {
        File file = new File(folder, "exception-" + exceptionCount++ + ".log");
        file.createNewFile();

        FileOutputStream output = new FileOutputStream(file, true);
        PrintStream stream = new PrintStream(output);
        exception.printStackTrace(stream);

        output.close();
        stream.close();

        System.err.println("An exception has been logged to " + file.getName());
    }
}
