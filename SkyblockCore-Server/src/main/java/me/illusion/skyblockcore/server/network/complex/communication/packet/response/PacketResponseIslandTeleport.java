package me.illusion.skyblockcore.server.network.complex.communication.packet.response;

import java.util.UUID;
import me.illusion.skyblockcore.common.packet.Packet;
import me.illusion.skyblockcore.common.packet.stream.FriendlyByteInputStream;
import me.illusion.skyblockcore.common.packet.stream.FriendlyByteOutputStream;
import me.illusion.skyblockcore.server.network.complex.communication.packet.request.PacketRequestIslandTeleport;

/**
 * Packet sent to another instance in response to a {@link PacketRequestIslandTeleport}.
 */
public class PacketResponseIslandTeleport extends Packet {

    private UUID playerId;
    private boolean allowed;

    public PacketResponseIslandTeleport(UUID playerId, boolean allowed) {
        this.playerId = playerId;
        this.allowed = allowed;
    }

    public PacketResponseIslandTeleport() {

    }

    @Override
    protected void read(FriendlyByteInputStream stream) {
        playerId = stream.readUUID();
        allowed = stream.readBoolean();
    }

    @Override
    protected void write(FriendlyByteOutputStream stream) {
        stream.writeUUID(playerId);
        stream.writeBoolean(allowed);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean isAllowed() {
        return allowed;
    }

}
