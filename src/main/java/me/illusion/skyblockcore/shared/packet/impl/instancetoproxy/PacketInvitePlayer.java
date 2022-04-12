package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

@Getter
public class PacketInvitePlayer extends ServerToProxyPacket {

    private final IslandInvite invite;

    public PacketInvitePlayer(byte[] bytes) {
        super(bytes);

        invite = new IslandInvite(readUUID(), readUUID(), readString());
    }

    public PacketInvitePlayer(IslandInvite invite) {
        super();

        this.invite = invite;

        writeUUID(invite.getInviteId());
        writeUUID(invite.getSender());
        writeString(invite.getTarget());
    }
}
