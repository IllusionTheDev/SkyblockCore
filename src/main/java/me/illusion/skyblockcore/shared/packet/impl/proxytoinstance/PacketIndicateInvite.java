package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

@Getter
public class PacketIndicateInvite extends ProxyToServerPacket {

    private final IslandInvite invite;


    public PacketIndicateInvite(byte[] bytes) {
        super(bytes);

        invite = new IslandInvite(readUUID(), readUUID(), readString(), readUUID(), readString(), readLong());
    }

    public PacketIndicateInvite(IslandInvite invite) {
        super((String) null);

        this.invite = invite;
        write();
    }

    @Override
    public void write() {
        writeUUID(invite.getInviteId());
        writeUUID(invite.getSender());
        writeString(invite.getSenderName());
        writeUUID(invite.getTarget());
        writeString(invite.getTargetName());
        writeLong(invite.getExpirationEpoch());
    }
}
