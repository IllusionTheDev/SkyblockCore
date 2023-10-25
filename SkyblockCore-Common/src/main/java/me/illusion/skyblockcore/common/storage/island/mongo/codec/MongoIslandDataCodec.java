package me.illusion.skyblockcore.common.storage.island.mongo.codec;

import java.util.UUID;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.storage.island.mongo.MongoIslandStorage;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * This codec is used for serializing and deserializing {@link IslandData} objects.
 */
public class MongoIslandDataCodec implements Codec<IslandData> {

    public static final MongoIslandDataCodec INSTANCE = new MongoIslandDataCodec();

    private MongoIslandDataCodec() {
    }

    @Override
    public IslandData decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        UUID islandId = readUUID(MongoIslandStorage.ISLAND_ID, reader);
        UUID ownerId = readUUID(MongoIslandStorage.PROFILE_ID, reader);

        reader.readEndDocument();

        return new IslandData(islandId, ownerId);
    }

    @Override
    public void encode(BsonWriter writer, IslandData value, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writeUUID(MongoIslandStorage.ISLAND_ID, value.getIslandId(), writer);
        writeUUID(MongoIslandStorage.PROFILE_ID, value.getOwnerId(), writer);

        writer.writeEndDocument();
    }

    @Override
    public Class<IslandData> getEncoderClass() {
        return IslandData.class;
    }

    private void writeUUID(String name, UUID uuid, BsonWriter writer) {
        writer.writeBinaryData(name, new BsonBinary(uuid));
    }

    private UUID readUUID(String name, BsonReader reader) {
        return reader.readBinaryData(name).asUuid();
    }
}
