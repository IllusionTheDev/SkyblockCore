package me.illusion.skyblockcore.spigot.network.complex.communication.packet.announce;

import java.util.UUID;
import lombok.Getter;
import me.illusion.skyblockcore.common.communication.packet.Packet;

/**
 * This packet is sent to all servers when a player switches profiles. The other servers update their cached profile ID if the player is cached.
 */
@Getter
public class PacketAnnounceProfileUpdate extends Packet {

    private final UUID playerId;
    private final UUID oldProfileId;
    private final UUID newProfileId;

    public PacketAnnounceProfileUpdate(UUID playerId, UUID oldProfileId, UUID newProfileId) {
        this.playerId = playerId;
        this.oldProfileId = oldProfileId;
        this.newProfileId = newProfileId;

        writeUUID(playerId);
        writeUUID(oldProfileId);
        writeUUID(newProfileId);
    }

    public PacketAnnounceProfileUpdate(byte[] bytes) {
        super(bytes);

        this.playerId = readUUID();
        this.oldProfileId = readUUID();
        this.newProfileId = readUUID();

    }
}
