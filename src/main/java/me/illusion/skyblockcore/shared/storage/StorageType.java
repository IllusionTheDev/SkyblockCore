package me.illusion.skyblockcore.shared.storage;

import me.illusion.skyblockcore.shared.dependency.DependencyDownloader;
import me.illusion.skyblockcore.shared.storage.handler.*;

public enum StorageType {
    MYSQL(MySQLHandler.class),
    SQLITE(SQLiteHandler.class),
    MONGODB(MongoDBHandler.class, "com.mongodb.MongoClient"),
    FILE(FileStorageHandler.class),
    S3(S3StorageHandler.class, "com.amazonaws.services.s3.AmazonS3");

    final Class<? extends StorageHandler> handlerClass;

    private String className;

    StorageType(Class<? extends StorageHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    StorageType(Class<? extends StorageHandler> handlerClass, String className) {
        this.handlerClass = handlerClass;
        this.className = className;
    }

    public void checkDependencies(DependencyDownloader downloader) {
        if (className == null)
            return;

        downloader.dependOn(className, "https://www.illusionthe.dev/dependencies/Skyblock.html", "SkyblockDependencies.html");
    }

    public Class<? extends StorageHandler> getHandlerClass() {
        return handlerClass;
    }
}
