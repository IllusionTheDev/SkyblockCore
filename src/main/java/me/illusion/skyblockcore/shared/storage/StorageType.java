package me.illusion.skyblockcore.shared.storage;

import me.illusion.skyblockcore.shared.storage.handler.MongoDBHandler;
import me.illusion.skyblockcore.shared.storage.handler.MySQLHandler;
import me.illusion.skyblockcore.shared.storage.handler.SQLiteHandler;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;

public enum StorageType {
    MYSQL(MySQLHandler.class),
    SQLITE(SQLiteHandler.class),
    MONGODB(MongoDBHandler.class, "com.mongodb.MongoClient");

    final Class<? extends StorageHandler> handlerClass;

    private String className;

    StorageType(Class<? extends StorageHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    StorageType(Class<? extends StorageHandler> handlerClass, String className) {
        this.handlerClass = handlerClass;
        this.className = className;
    }

    public void checkDependencies(SkyblockPlugin main) {
        if (className == null)
            return;

        main.getDependencyDownloader().dependOn(className, "https://www.illusionthe.dev/dependencies/Skyblock.html", "SkyblockDependencies.html");
    }

    public Class<? extends StorageHandler> getHandlerClass() {
        return handlerClass;
    }
}
