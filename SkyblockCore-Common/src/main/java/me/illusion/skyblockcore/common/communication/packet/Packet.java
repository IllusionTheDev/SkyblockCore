package me.illusion.skyblockcore.common.communication.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import lombok.SneakyThrows;

public abstract class Packet {

    private final byte identifier;
    private final UUID uuid;

    private ByteArrayDataOutput stream;
    private ByteArrayDataInput input;

    public Packet(byte[] bytes) {
        input = ByteStreams.newDataInput(bytes);
        identifier = readByte();
        uuid = readUUID();
    }

    public Packet() {
        this.identifier = PacketManager.getIdentifier(getClass());
        this.uuid = UUID.randomUUID();

        validateStream();
        writeByte(identifier);
        writeUUID(uuid);
    }

    protected byte getIdentifier() {
        return identifier;
    }

    private void validateStream() {
        if (stream == null) {
            stream = ByteStreams.newDataOutput();
        }
    }

    protected void writeByte(byte value) {
        validateStream();
        stream.writeByte(value);
    }

    protected void writeByteArray(byte[] bytes) {
        validateStream();
        writeInt(bytes.length);
        stream.write(bytes);
    }

    protected void writeShort(short value) {
        validateStream();
        stream.writeShort(value);
    }

    protected void writeInt(int value) {
        validateStream();
        stream.writeInt(value);
    }

    protected void writeLong(long value) {
        validateStream();
        stream.writeLong(value);
    }

    protected void writeFloat(float value) {
        validateStream();
        stream.writeFloat(value);
    }

    protected void writeDouble(double value) {
        validateStream();
        stream.writeDouble(value);
    }

    protected void writeChar(char value) {
        validateStream();
        stream.writeChar(value);
    }

    protected void writeString(String string) {
        validateStream();
        stream.writeUTF(string);
    }

    protected void writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    @SneakyThrows
    protected void writeObject(Object object) {
        validateStream();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byte[] bytes = bos.toByteArray();
            writeByteArray(bytes);
            out.close();
        }
        // ignore close exception
    }


    protected byte readByte() {
        return input.readByte();
    }

    protected byte[] readByteArray() {
        int arraySize = readInt();
        byte[] array = new byte[arraySize];

        for (int index = 0; index < arraySize; index++) {
            array[index] = readByte();
        }

        return array;
    }

    protected short readShort() {
        return input.readShort();
    }

    protected int readInt() {
        return input.readInt();
    }

    protected long readLong() {
        return input.readLong();
    }

    protected String readString() {
        return input.readUTF();
    }

    protected UUID readUUID() {
        return new UUID(readLong(), readLong());
    }


    @SneakyThrows
    protected Object readObject() {
        byte[] bytes = readByteArray();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(
            bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }

        // ignore close exception
    }

    public byte[] getAllBytes() {
        return stream.toByteArray();
    }

    public UUID getPacketId() {
        return uuid;
    }
}
