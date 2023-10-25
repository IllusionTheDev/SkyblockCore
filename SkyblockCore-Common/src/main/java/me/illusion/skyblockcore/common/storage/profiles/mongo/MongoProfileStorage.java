package me.illusion.skyblockcore.common.storage.profiles.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.database.persistence.mongo.MongoPersistenceDatabase;
import me.illusion.skyblockcore.common.database.persistence.mongo.MongoUUIDCodec;
import me.illusion.skyblockcore.common.storage.profiles.SkyblockProfileStorage;
import org.bson.codecs.Codec;

public class MongoProfileStorage extends MongoPersistenceDatabase implements SkyblockProfileStorage {

    private MongoCollection<UUID> profileIdCollection; // Player ID : Profile ID

    @Override
    protected String getDefaultCollection() {
        return "profiles";
    }

    @Override
    protected void initializeCollections() {
        profileIdCollection = database.getCollection("profileIds", UUID.class);
    }

    @Override
    protected List<Codec<?>> getCodecs() {
        return List.of(MongoUUIDCodec.INSTANCE);
    }

    @Override
    public CompletableFuture<UUID> getProfileId(UUID playerId) {
        return associate(() -> profileIdCollection.find(Filters.eq(playerId)).first());
    }

    @Override
    public CompletableFuture<Void> setProfileId(UUID playerId, UUID profileId) {
        return associate(() -> {
            profileIdCollection.replaceOne(Filters.eq(playerId), profileId, UPSERT); // Needs braces due to UpdateResult
        });
    }

    @Override
    public CompletableFuture<Map<UUID, UUID>> getAllProfileIds() {
        return associate(() -> {
            Map<UUID, UUID> map = new ConcurrentHashMap<>();

            for (UUID playerId : profileIdCollection.find()) {
                map.put(playerId, profileIdCollection.find(Filters.eq(playerId)).first());
            }

            return map;
        });
    }

    @Override
    public CompletableFuture<Void> setAllProfileIds(Map<UUID, UUID> profileIds) {
        return associate(() -> {
            for (Map.Entry<UUID, UUID> entry : profileIds.entrySet()) {
                profileIdCollection.replaceOne(Filters.eq(entry.getKey()), entry.getValue(), UPSERT);
            }
        });
    }
}
