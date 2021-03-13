package me.illusion.skyblockcore.shared.storage.handler;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDBHandler implements StorageHandler {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> islandStorage;

    @Override
    public CompletableFuture<Boolean> setup(String ip, int port, String database, String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                mongoClient = new MongoClient(ip, port);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }

            this.database = mongoClient.getDatabase(database);
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


    @Override
    public boolean isFileBased() {
        return false;
    }
}
