package me.illusion.skyblockcore.common.packet;

import java.util.UUID;
import me.illusion.skyblockcore.common.packet.stream.FriendlyByteInputStream;
import me.illusion.skyblockcore.common.packet.stream.FriendlyByteOutputStream;

public abstract class Packet {

    private UUID id;

    protected abstract void read(FriendlyByteInputStream stream);

    protected abstract void write(FriendlyByteOutputStream stream);

    public final void readData(FriendlyByteInputStream stream) {
        id = stream.readUUID();
        read(stream);
    }

    public final byte[] writeData(FriendlyByteOutputStream stream) {
        if (id == null) {
            id = UUID.randomUUID();
        }

        stream.writeUUID(id);
        write(stream);
        return stream.toByteArray();
    }
}
