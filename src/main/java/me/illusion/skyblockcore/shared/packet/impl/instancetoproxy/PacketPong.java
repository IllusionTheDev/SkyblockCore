package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

@Getter
public class PacketPong extends ServerToProxyPacket {

    private final byte islandCount;
    private final byte islandCapacity;

    public PacketPong(byte[] bytes) {
        super(bytes);

        islandCount = readByte();
        islandCapacity = readByte();
    }

    public PacketPong(byte islandCount, byte islandCapacity) {
        super();
        this.islandCount = islandCount;
        this.islandCapacity = islandCapacity;

        write();
    }

    @Override
    public void write() {
        writeByte(islandCount);
        writeByte(islandCapacity);
    }
}
