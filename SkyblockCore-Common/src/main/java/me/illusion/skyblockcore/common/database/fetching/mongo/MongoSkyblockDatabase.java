package me.illusion.skyblockcore.common.database.fetching.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.database.fetching.mongo.codec.MongoIslandDataCodec;
import me.illusion.skyblockcore.common.database.fetching.mongo.codec.MongoUUIDCodec;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * The mongodb implementation of {@link SkyblockFetchingDatabase}.
 */
public class MongoSkyblockDatabase implements SkyblockFetchingDatabase {

    private final Set<CompletableFuture<?>> futures = ConcurrentHashMap.newKeySet();

    private MongoClient mongoClient;

    private MongoCollection<IslandData> islandDataCollection;
    private MongoCollection<UUID> islandIdCollection;
    private MongoCollection<UUID> profileIdCollection;

    @Override
    public String getName() {
        return "mongodb";
    }

    @Override
    public CompletableFuture<Boolean> enable(ReadOnlyConfigurationSection properties) {
        return associate(() -> {
            String connectionString = properties.getString("connection-string");

            if (connectionString == null) {
                String ip = properties.getString("ip");
                int port = properties.getInt("port");
                String authsource = properties.getString("auth-source", "admin");
                String username = properties.getString("username");
                String password = properties.getString("password");
                boolean ssl = properties.getBoolean("ssl", false);

                connectionString = createConnectionString(ip, port, authsource, username, password, ssl);
            }

            String database = properties.getString("database", "skyblock");
            String collectionName = properties.getString("collection", "islands");

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
                profileIdCollection = mongoClient.getDatabase(database).getCollection("profile_ids", UUID.class);

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

    @Override
    public CompletableFuture<UUID> getProfileId(UUID playerId) {
        return associate(() -> {
            Document filter = new Document("playerId", playerId);

            for (UUID id : profileIdCollection.find(filter)) {
                return id;
            }

            return null; // What if there is no profile id?
        });
    }

    @Override
    public CompletableFuture<Void> setProfileId(UUID playerId, UUID profileId) {
        return associate(() -> {
            Document filter = new Document("playerId", playerId);
            profileIdCollection.replaceOne(filter, profileId);
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

}
