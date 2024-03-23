package me.illusion.skyblockcore.common.packet.stream;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.function.Consumer;

public class FriendlyByteOutputStream {

    private final ByteArrayOutputStream stream;

    public FriendlyByteOutputStream() {
        this.stream = new ByteArrayOutputStream();
    }

    // CUSTOM METHODS BELOW

    public void writeUUID(java.util.UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public <T extends Enum<T>> void writeEnum(T value) {
        writeString(value.name());
    }

    // DEFAULT METHODS BELOW

    public void writeBytes(byte[] data) {
        try {
            stream.write(data);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInt(int value) {
        stream.write((value >> 24) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
    }

    public void writeLong(long value) {
        stream.write((int) (value >> 56) & 0xFF);
        stream.write((int) (value >> 48) & 0xFF);
        stream.write((int) (value >> 40) & 0xFF);
        stream.write((int) (value >> 32) & 0xFF);
        stream.write((int) (value >> 24) & 0xFF);
        stream.write((int) (value >> 16) & 0xFF);
        stream.write((int) (value >> 8) & 0xFF);
        stream.write((int) value & 0xFF);
    }

    public void writeShort(short value) {
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
    }

    public void writeBoolean(boolean value) {
        stream.write(value ? 1 : 0);
    }

    public void writeString(String value) {
        byte[] data = value.getBytes();
        writeInt(data.length);
        writeBytes(data);
    }

    public void writeFloat(float value) {
        writeInt(Float.floatToIntBits(value));
    }

    public void writeDouble(double value) {
        writeLong(Double.doubleToLongBits(value));
    }

    public <T> void writeNullable(T value, Consumer<T> consumer) {
        writeBoolean(value != null);
        if (value != null) {
            consumer.accept(value);
        }
    }

    public void writeByteArray(byte[] data) {
        writeInt(data.length);
        writeBytes(data);
    }

    public <T> void writeCollection(Collection<T> collection, Consumer<T> consumer) {
        writeInt(collection.size());
        for (T t : collection) {
            consumer.accept(t);
        }
    }

    public byte[] toByteArray() {
        return stream.toByteArray();
    }

}
