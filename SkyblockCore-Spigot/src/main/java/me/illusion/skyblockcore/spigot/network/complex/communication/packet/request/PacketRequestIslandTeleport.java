package me.illusion.skyblockcore.spigot.network.complex.communication.packet.request;

import java.util.UUID;
import me.illusion.skyblockcore.common.communication.packet.Packet;

public class PacketRequestIslandTeleport extends Packet {

    private final String originServer;
    private final UUID playerId;
    private final UUID islandId;

    public PacketRequestIslandTeleport(String originServer, UUID playerId, UUID islandId) {
        this.originServer = originServer;
        this.playerId = playerId;
        this.islandId = islandId;

        writeString(originServer);
        writeUUID(playerId);
        writeUUID(islandId);
    }

    public PacketRequestIslandTeleport(byte[] bytes) {
        super(bytes);

        this.originServer = readString();
        this.playerId = readUUID();
        this.islandId = readUUID();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getIslandId() {
        return islandId;
    }

    public String getOriginServer() {
        return originServer;
    }
}
