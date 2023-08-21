package me.illusion.skyblockcore.server.network.complex.communication.packet.response;

import java.util.UUID;
import me.illusion.skyblockcore.common.communication.packet.Packet;
import me.illusion.skyblockcore.server.network.complex.communication.packet.request.PacketRequestIslandTeleport;

/**
 * Packet sent to another instance in response to a {@link PacketRequestIslandTeleport}.
 */
public class PacketResponseIslandTeleport extends Packet {

    private final UUID playerId;
    private final boolean allowed;

    public PacketResponseIslandTeleport(UUID playerId, boolean allowed) {
        this.playerId = playerId;
        this.allowed = allowed;

        writeUUID(playerId);
        writeByte((byte) (allowed ? 1 : 0));
    }

    public PacketResponseIslandTeleport(byte[] bytes) {
        super(bytes);

        this.playerId = readUUID();
        this.allowed = readByte() == 1;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean isAllowed() {
        return allowed;
    }

}
