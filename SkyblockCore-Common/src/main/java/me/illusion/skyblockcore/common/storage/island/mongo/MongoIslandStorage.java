package me.illusion.skyblockcore.common.storage.island.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.databaserewrite.persistence.mongo.MongoPersistenceDatabase;
import me.illusion.skyblockcore.common.storage.island.SkyblockIslandStorage;
import me.illusion.skyblockcore.common.storage.island.mongo.codec.MongoIslandDataCodec;
import me.illusion.skyblockcore.common.storage.island.mongo.codec.MongoUUIDCodec;
import org.bson.codecs.Codec;

public class MongoIslandStorage extends MongoPersistenceDatabase implements SkyblockIslandStorage {

    public static final String PROFILE_ID = "profileId";
    public static final String ISLAND_ID = "islandId";

    private static final ReplaceOptions UPSERT = new ReplaceOptions().upsert(true);

    private MongoCollection<UUID> islandIdCollection; // Profile ID : Island ID
    private MongoCollection<IslandData> islandDataCollection; // Island ID : Island Data

    @Override
    public CompletableFuture<UUID> getIslandId(UUID profileId) {
        return associate(() -> islandIdCollection.find(Filters.eq(PROFILE_ID, profileId)).first());
    }

    @Override
    public CompletableFuture<IslandData> getIslandData(UUID islandId) {
        return associate(() -> islandDataCollection.find(Filters.eq(ISLAND_ID, islandId)).first());
    }

    @Override
    public CompletableFuture<Void> saveIslandData(IslandData data) {
        return associate(() -> {
            islandIdCollection.replaceOne(Filters.eq(PROFILE_ID, data.getOwnerId()), data.getIslandId(), UPSERT);
            islandDataCollection.replaceOne(Filters.eq(ISLAND_ID, data.getIslandId()), data, UPSERT);
        });
    }

    @Override
    public CompletableFuture<Void> deleteIslandData(UUID islandId) {
        return associate(() -> {
            islandIdCollection.deleteOne(Filters.eq(ISLAND_ID, islandId));
            islandDataCollection.deleteOne(Filters.eq(ISLAND_ID, islandId));
        });
    }

    @Override
    public CompletableFuture<Collection<IslandData>> getAllIslandData() {
        return associate(() -> islandDataCollection.find().into(new ArrayList<>()));
    }

    @Override
    public CompletableFuture<Void> saveAllIslandData(Collection<IslandData> data) {
        return associate(() -> {
            for (IslandData islandData : data) {
                islandIdCollection.replaceOne(Filters.eq(PROFILE_ID, islandData.getOwnerId()), islandData.getIslandId(), UPSERT);
                islandDataCollection.replaceOne(Filters.eq(ISLAND_ID, islandData.getIslandId()), islandData, UPSERT);
            }
        });
    }

    @Override
    protected String getDefaultCollection() {
        return "islands";
    }

    @Override
    protected List<Codec<?>> getCodecs() {
        return List.of(
            MongoIslandDataCodec.INSTANCE,
            MongoUUIDCodec.INSTANCE
        );
    }

    @Override
    protected void initializeCollections() {
        islandDataCollection = database.getCollection(collectionName, IslandData.class);
        islandIdCollection = database.getCollection(collectionName, UUID.class);
    }
}
