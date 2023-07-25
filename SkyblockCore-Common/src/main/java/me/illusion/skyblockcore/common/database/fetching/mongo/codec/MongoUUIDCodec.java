package me.illusion.skyblockcore.common.database.fetching.mongo.codec;

import java.util.UUID;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * This codec is used for serializing and deserializing {@link UUID} objects.
 */
public class MongoUUIDCodec implements Codec<UUID> {

    public static final MongoUUIDCodec INSTANCE = new MongoUUIDCodec();

    private MongoUUIDCodec() {
    }

    @Override
    public UUID decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readBinaryData().asUuid();
    }

    @Override
    public void encode(BsonWriter writer, UUID value, EncoderContext encoderContext) {
        writer.writeBinaryData(new BsonBinary(value));
    }

    @Override
    public Class<UUID> getEncoderClass() {
        return UUID.class;
    }
}
