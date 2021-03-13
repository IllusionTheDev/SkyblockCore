package me.illusion.skyblockcore.shared.storage;

import me.illusion.skyblockcore.shared.storage.handler.MongoDBHandler;
import me.illusion.skyblockcore.shared.storage.handler.MySQLHandler;
import me.illusion.skyblockcore.shared.storage.handler.SQLiteHandler;

public enum StorageType {
    MYSQL(MySQLHandler.class),
    SQLITE(SQLiteHandler.class),
    MONGODB(MongoDBHandler.class);

    Class<? extends StorageHandler> handlerClass;

    StorageType(Class<? extends StorageHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public Class<? extends StorageHandler> getHandlerClass() {
        return handlerClass;
    }
}
