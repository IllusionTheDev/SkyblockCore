package me.illusion.skyblockcore.shared.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.shared.packet.data.PacketDirection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

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

    }

    public Packet(PacketDirection direction) {

        this.identifier = PacketManager.getIdentifier(getClass());
        this.direction = direction;

        validateStream();
        writeByte(identifier);
        writeByte(direction.getIndex());
    }

    protected byte getIdentifier() {
        return identifier;
    }

    protected PacketDirection getDirection() {
        return direction;
    }

    private void validateStream() {
        if (stream == null)
            stream = ByteStreams.newDataOutput();
    }

    protected void writeBoolean(boolean value) {
        validateStream();
        stream.writeBoolean(value);
    }

    protected void writeByte(byte value) {
        validateStream();
        stream.writeByte(value);
    }

    protected void writeByteArray(byte... bytes) {
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

    protected void writeBungeeText(BaseComponent... text) {
        writeString(ComponentSerializer.toString(text));
    }


    @SneakyThrows
    protected void writeObject(Object object) {
        validateStream();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            out.flush();
            byte[] bytes = bos.toByteArray();
            writeByteArray(bytes);
        }
        // ignore close exception
    }


    protected boolean readBoolean() {
        return input.readBoolean();
    }

    protected byte readByte() {
        return input.readByte();
    }

    protected byte[] readByteArray() {
        int arraySize = readInt();
        byte[] array = new byte[arraySize];

        for (int index = 0; index < arraySize; index++)
            array[index] = readByte();

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

    protected BaseComponent[] readBungeeText() {
        return ComponentSerializer.parse(readString());
    }

    @SneakyThrows
    protected Object readObject() {
        byte[] bytes = readByteArray();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }

        // ignore close exception
    }

    public byte[] getAllBytes() {
        return stream.toByteArray();
    }
}
