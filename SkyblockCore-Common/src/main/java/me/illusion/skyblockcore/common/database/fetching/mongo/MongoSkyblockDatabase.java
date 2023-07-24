package me.illusion.skyblockcore.common.database.fetching.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.database.fetching.mongo.codec.MongoIslandDataCodec;
import me.illusion.skyblockcore.common.database.fetching.mongo.codec.MongoUUIDCodec;
import me.illusion.skyblockcore.common.database.structure.SkyblockDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class MongoSkyblockDatabase implements SkyblockDatabase {

    private final Set<CompletableFuture<?>> futures = ConcurrentHashMap.newKeySet();

    private MongoClient mongoClient;
    private MongoCollection<IslandData> islandDataCollection;
    private MongoCollection<UUID> islandIdCollection;

    @Override
    public String getName() {
        return "mongodb";
    }

    @Override
    public CompletableFuture<Boolean> enable(Map<String, ?> properties) {
        return associate(() -> {
            String connectionString = getOrDefault(properties, "connection_string");

            if (connectionString == null) {
                String ip = getOrDefault(properties, "ip");
                int port = getOrDefault(properties, "port");
                String authsource = getOrDefault(properties, "auth-source");
                String username = getOrDefault(properties, "username");
                String password = getOrDefault(properties, "password");
                boolean ssl = getOrDefault(properties, "ssl", false);

                connectionString = createConnectionString(ip, port, authsource, username, password, ssl);
            }

            String database = getOrDefault(properties, "database", "skyblock");
            String collectionName = getOrDefault(properties, "collection", "skyblock_data");

            CodecRegistry codecs = CodecRegistries.fromCodecs(
                MongoIslandDataCodec.INSTANCE,
                MongoUUIDCodec.INSTANCE
            );

            try {
                MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .codecRegistry(codecs)
                    .build();

                mongoClient = MongoClients.create(settings);

                islandDataCollection = mongoClient.getDatabase(database)
                    .getCollection(collectionName, IslandData.class); // if the collection doesn't exist, it will be created
                islandIdCollection = mongoClient.getDatabase(database).getCollection("island_ids", UUID.class);

                // validate the session
                mongoClient.listDatabaseNames().first(); // throws an exception if the connection is invalid
                return true;
            } catch (Exception expected) { // catching MongoException doesn't work for some reason
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<UUID> fetchIslandId(UUID playerId) {
        return associate(() -> {
            Document filter = new Document("ownerId", playerId);

            for (UUID id : islandIdCollection.find(filter)) {
                return id;
            }

            return null; // What if there is no island id?
        });
    }

    @Override
    public CompletableFuture<IslandData> fetchIslandData(UUID islandId) {
        return associate(() -> {
            Document filter = new Document("islandId", islandId);

            for (IslandData data : islandDataCollection.find(filter)) {
                return data;
            }

            return null; // What if there is no island data?
        });
    }

    @Override
    public CompletableFuture<Void> saveIslandData(IslandData data) {
        return associate(() -> {
            Document filter = new Document("islandId", data.getIslandId());
            islandDataCollection.replaceOne(filter, data);

            // Let's also set the island id to the owner
            Document idFilter = new Document("ownerId", data.getOwnerId());
            islandIdCollection.replaceOne(idFilter, data.getIslandId());
        });
    }

    @Override
    public CompletableFuture<Void> deleteIslandData(UUID islandId) {
        return associate(() -> {
            Document filter = new Document("islandId", islandId);
            islandDataCollection.deleteOne(filter);
            islandIdCollection.deleteOne(filter);
        });
    }

    private <T> CompletableFuture<T> associate(Supplier<T> supplier) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier);

        future.thenRun(() -> futures.remove(future));
        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        futures.add(future);
        return future;
    }

    private CompletableFuture<Void> associate(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable);

        future.thenRun(() -> futures.remove(future));
        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        futures.add(future);
        return future;
    }


    @Override
    public CompletableFuture<Void> flush() {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private String createConnectionString(String ip, int port, String authsource, String username, String password, boolean ssl) {
        StringBuilder builder = new StringBuilder();
        builder.append("mongodb://");
        if (username != null && !username.isEmpty()) {
            builder.append(username);
            if (password != null && !password.isEmpty()) {
                builder.append(":").append(password);
            }
            builder.append("@");
        }

        builder.append(ip).append(":").append(port);

        if (authsource != null && !authsource.isEmpty()) {
            builder.append("/?authSource=").append(authsource);
        }

        if (ssl) {
            builder.append("&ssl=true");
        }

        return builder.toString();
    }

    private <T> T getOrDefault(Map<String, ?> map, String key, T defaultValue) {
        Object value = map.get(key);

        if (value == null) {
            return defaultValue;
        }

        return (T) value;
    }

    private <T> T getOrDefault(Map<String, ?> map, String key) {
        return getOrDefault(map, key, null);
    }

}
