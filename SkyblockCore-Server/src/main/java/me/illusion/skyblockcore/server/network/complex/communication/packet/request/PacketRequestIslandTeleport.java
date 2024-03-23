package me.illusion.skyblockcore.server.network.complex.communication.packet.request;

import java.util.UUID;
import me.illusion.skyblockcore.common.packet.Packet;
import me.illusion.skyblockcore.common.packet.stream.FriendlyByteInputStream;
import me.illusion.skyblockcore.common.packet.stream.FriendlyByteOutputStream;

/**
 * Packet sent to another instance requesting to teleport a player to an island. This packet is sent when a player wants to join an island that is not loaded on
 * the current instance.
 */
public class PacketRequestIslandTeleport extends Packet {

    private String originServer;
    private UUID playerId;
    private UUID islandId;

    public PacketRequestIslandTeleport(String originServer, UUID playerId, UUID islandId) {
        this.originServer = originServer;
        this.playerId = playerId;
        this.islandId = islandId;
    }

    public PacketRequestIslandTeleport() {

    }

    @Override
    protected void read(FriendlyByteInputStream stream) {
        originServer = stream.readString();
        playerId = stream.readUUID();
        islandId = stream.readUUID();
    }

    @Override
    protected void write(FriendlyByteOutputStream stream) {
        stream.writeString(originServer);
        stream.writeUUID(playerId);
        stream.writeUUID(islandId);
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
