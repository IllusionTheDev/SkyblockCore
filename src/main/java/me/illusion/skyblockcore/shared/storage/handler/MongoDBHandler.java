package me.illusion.skyblockcore.shared.storage.handler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.storage.StorageUtils;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import org.bson.Document;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDBHandler implements StorageHandler {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> islandStorage;

    @Override
    public CompletableFuture<Boolean> setup(File folder, Map<String, Object> map) {
        return CompletableFuture.supplyAsync(() -> {
            String ip = map.get("ip").toString();
            int port = Integer.parseInt(map.get("port").toString());
            String database = map.get("database").toString();

            String username = map.getOrDefault("username", "").toString();
            String password = map.getOrDefault("password", "").toString();

            try {
                mongoClient = new MongoClient(new MongoClientURI("mongodb://" + username + ":" + password + "@" + ip + ":" + port + "/" + database));
            } catch (Exception exception) {
                ExceptionLogger.log(exception);
                return false;
            }

            this.database = mongoClient.getDatabase(database);
            this.database.createCollection("islandData");
            islandStorage = this.database.getCollection("islandData");
            return true;
        });
    }

    @Override
    public CompletableFuture<SkyblockSerializable> get(UUID uuid, String category) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = new Document(category + "-uuid", uuid.toString());

            Document found = islandStorage.find(document).first();

            if (found == null)
                return null;

            Map<String, Object> map = new HashMap<>();

            for (String key : found.keySet()) {
                map.put(key, found.get(key));
            }

            return StorageUtils.unserialize(map);
        });
    }

    @Override
    public CompletableFuture<Void> save(UUID uuid, SkyblockSerializable object, String category) {
        return CompletableFuture.runAsync(() -> {
            Document document = new Document(category + "-uuid", uuid.toString());

            Map<String, Object> processed = process(object);

            for (Map.Entry<String, Object> entry : processed.entrySet()) {
                document.append(entry.getKey(), entry.getValue());
            }

            islandStorage.insertOne(document);
        });
    }

    @Override
    public CompletableFuture<Void> delete(UUID uuid, String category) {
        return CompletableFuture.runAsync(() -> {
            Document document = new Document(category + "-uuid", uuid.toString());

            islandStorage.deleteOne(document);
        });
    }
}
