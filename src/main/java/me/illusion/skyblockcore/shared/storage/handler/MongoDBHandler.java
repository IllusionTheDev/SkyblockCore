package me.illusion.skyblockcore.shared.storage.handler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import org.bson.Document;

import java.io.File;
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
    public CompletableFuture<Object> get(UUID uuid, String category) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = new Document(category + "-uuid", uuid.toString());

            Document found = islandStorage.find(document).first();

            if (found == null)
                return null;

            return found.get("value");
        });
    }

    @Override
    public CompletableFuture<Void> save(UUID uuid, Object object, String category) {
        return CompletableFuture.runAsync(() -> {
            Document document = new Document(category + "-uuid", uuid.toString())
                    .append("value", object);

            islandStorage.insertOne(document);
        });
    }

}
