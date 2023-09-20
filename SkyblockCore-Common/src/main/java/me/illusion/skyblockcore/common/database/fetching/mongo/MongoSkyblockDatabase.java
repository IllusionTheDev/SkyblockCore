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
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.database.fetching.AbstractSkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.database.fetching.mongo.codec.MongoIslandDataCodec;
import me.illusion.skyblockcore.common.database.fetching.mongo.codec.MongoUUIDCodec;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * The mongodb implementation of {@link SkyblockFetchingDatabase}.
 */
public class MongoSkyblockDatabase extends AbstractSkyblockFetchingDatabase {

    private final Set<CompletableFuture<?>> futures = ConcurrentHashMap.newKeySet();

    private MongoClient mongoClient;

    private MongoCollection<IslandData> islandDataCollection; // Island ID : Island Data
    private MongoCollection<UUID> islandIdCollection; // Profile ID : Island ID
    private MongoCollection<UUID> profileIdCollection; // Player ID : Profile ID

    public MongoSkyblockDatabase() {
        addTag(SkyblockDatabaseTag.REMOTE);
        addTag(SkyblockDatabaseTag.NO_SQL);
    }

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
    public CompletableFuture<UUID> fetchIslandId(UUID profileId) {
        return associate(() -> islandIdCollection.find(ownerId(profileId)).first());
    }

    @Override
    public CompletableFuture<IslandData> fetchIslandData(UUID islandId) {
        return associate(() -> islandDataCollection.find(islandId(islandId)).first());
    }

    @Override
    public CompletableFuture<Void> saveIslandData(IslandData data) {
        return associate(() -> {
            islandDataCollection.replaceOne(islandId(data.getIslandId()), data);

            // Let's also set the island id to the owner
            islandIdCollection.replaceOne(ownerId(data.getOwnerId()), data.getIslandId());
        });
    }

    @Override
    public CompletableFuture<Void> deleteIslandData(UUID islandId) {
        return associate(() -> {
            Document filter = islandId(islandId);
            islandDataCollection.deleteOne(filter);
            islandIdCollection.deleteOne(filter);
        });
    }

    @Override
    public CompletableFuture<UUID> getProfileId(UUID playerId) {
        return associate(() -> profileIdCollection.find(playerId(playerId)).first());
    }

    @Override
    public CompletableFuture<Void> setProfileId(UUID playerId, UUID profileId) {
        return associate((Runnable) () -> profileIdCollection.replaceOne(playerId(playerId), profileId));
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

    private Document islandId(UUID islandId) {
        return new Document("islandId", islandId);
    }

    private Document playerId(UUID playerId) {
        return new Document("playerId", playerId);
    }

    private Document ownerId(UUID ownerId) {
        return new Document("ownerId", ownerId);
    }
}
