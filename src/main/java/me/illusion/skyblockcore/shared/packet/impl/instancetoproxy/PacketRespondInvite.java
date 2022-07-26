package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketInviteResponse;

@Getter
public class PacketRespondInvite extends ServerToProxyPacket {

    private final IslandInvite invite;
    private final PacketInviteResponse.Response response;

    public PacketRespondInvite(byte[] bytes) {
        super(bytes);

        invite = new IslandInvite(readUUID(), readUUID(), readString(), readUUID(), readString(), readLong());
        response = PacketInviteResponse.Response.getResponse(readInt());
    }

    public PacketRespondInvite(IslandInvite invite, PacketInviteResponse.Response response) {
        this.invite = invite;
        this.response = response;

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
        writeInt(response.ordinal());
    }
}
