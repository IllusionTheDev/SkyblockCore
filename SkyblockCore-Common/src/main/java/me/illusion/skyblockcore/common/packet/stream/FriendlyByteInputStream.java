package me.illusion.skyblockcore.common.packet.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class FriendlyByteInputStream {

    private final ByteArrayInputStream stream;

    public FriendlyByteInputStream(byte[] data) {
        this.stream = new ByteArrayInputStream(data);
    }

    // CUSTOM METHODS BELOW

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    public <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return Enum.valueOf(clazz, readString());
    }

    // DEFAULT METHODS BELOW

    public byte[] readBytes(int length) {
        byte[] data = new byte[length];
        stream.read(data, 0, length);
        return data;
    }

    public int readInt() {
        return (stream.read() << 24) | (stream.read() << 16) | (stream.read() << 8) | stream.read();
    }

    public long readLong() {
        return
            ((long) stream.read() << 56) |
                ((long) stream.read() << 48) |
                ((long) stream.read() << 40) |
                ((long) stream.read() << 32) |
                ((long) stream.read() << 24) |
                ((long) stream.read() << 16) |
                ((long) stream.read() << 8) |
                stream.read();
    }

    public short readShort() {
        return (short) ((stream.read() << 8) | stream.read());
    }

    public boolean readBoolean() {
        return stream.read() == 1;
    }

    public String readString() {
        int length = readInt();
        byte[] data = readBytes(length);
        return new String(data);
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public char readChar() {
        return (char) readShort();
    }

    public byte readByte() {
        return (byte) stream.read();
    }

    public void close() throws IOException {
        stream.close();
    }

    public <T> T readNullable(Supplier<T> supplier) {
        return readBoolean() ? supplier.get() : null;
    }

    public byte[] readByteArray() {
        int length = readInt();
        return readBytes(length);
    }

    public <T, C extends Collection<T>> C readCollection(C collection, Supplier<T> supplier) {
        int size = readInt();
        for (int i = 0; i < size; i++) {
            collection.add(supplier.get());
        }
        return collection;
    }

    public int available() {
        return stream.available();
    }

}
