package me.illusion.skyblockcore.common.database.fetching.mongo;

import java.util.UUID;
import me.illusion.skyblockcore.common.data.IslandData;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class MongoIslandDataCodec implements Codec<IslandData> {

    public static final MongoIslandDataCodec INSTANCE = new MongoIslandDataCodec();

    private MongoIslandDataCodec() {
    }

    @Override
    public IslandData decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        UUID islandId = readUUID("islandId", reader);
        UUID ownerId = readUUID("ownerId", reader);

        reader.readEndDocument();

        return new IslandData(islandId, ownerId);
    }

    @Override
    public void encode(BsonWriter writer, IslandData value, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writeUUID("islandId", value.getIslandId(), writer);
        writeUUID("ownerId", value.getOwnerId(), writer);

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
