package me.illusion.skyblockcore.shared.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.shared.packet.data.PacketDirection;
import me.illusion.skyblockcore.shared.packet.event.PacketReceiveEvent;

import java.io.*;
import java.util.UUID;

public abstract class Packet {

    private final byte identifier;
    private final PacketDirection direction;

    private ByteArrayDataOutput stream;
    private ByteArrayDataInput input;

    public Packet(byte[] bytes) {
        input = ByteStreams.newDataInput(bytes);
        identifier = readByte();
        direction = PacketDirection.fromIndex(readByte());

        new PacketReceiveEvent(this);
    }

    public Packet(byte identifier, PacketDirection direction) {
        this.identifier = identifier;
        this.direction = direction;

        validateStream();
        writeByte(identifier);
        writeByte(direction.getIndex());
    }

    public byte getIdentifier() {
        return identifier;
    }

    public PacketDirection getDirection() {
        return direction;
    }

    private void validateStream() {
        if (stream == null)
            stream = ByteStreams.newDataOutput();
    }

    public void writeByte(byte value) {
        validateStream();
        stream.writeByte(value);
    }

    public void writeByteArray(byte[] bytes) {
        validateStream();
        writeInt(bytes.length);
        stream.write(bytes);
    }

    public void writeShort(short value) {
        validateStream();
        stream.writeShort(value);
    }

    public void writeInt(int value) {
        validateStream();
        stream.writeInt(value);
    }

    public void writeLong(long value) {
        validateStream();
        stream.writeLong(value);
    }

    public void writeFloat(float value) {
        validateStream();
        stream.writeFloat(value);
    }

    public void writeDouble(double value) {
        validateStream();
        stream.writeDouble(value);
    }

    public void writeChar(char value) {
        validateStream();
        stream.writeChar(value);
    }

    public void writeString(String string) {
        validateStream();
        stream.writeUTF(string);
    }

    public void writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }


    @SneakyThrows
    public void writeObject(Object object) {
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


    public byte readByte() {
        return input.readByte();
    }

    public byte[] readByteArray() {
        int arraySize = readInt();
        byte[] array = new byte[arraySize];

        for (int index = 0; index < arraySize; index++)
            array[index] = readByte();

        return array;
    }

    public short readShort() {
        return input.readShort();
    }

    public int readInt() {
        return input.readInt();
    }

    public long readLong() {
        return input.readLong();
    }

    public String readString() {
        return input.readUTF();
    }

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    @SneakyThrows
    public Object readObject() {
        byte[] bytes = readByteArray();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }

        // ignore close exception
    }
}
