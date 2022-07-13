package me.illusion.skyblockcore.shared.packet.impl.proxytoproxy.request;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ProxyToProxyPacket;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.UUID;

@Getter
public class PacketRequestMessageSend extends ProxyToProxyPacket {

    private final UUID uuid;
    private final BaseComponent[] message;

    public PacketRequestMessageSend(byte[] bytes) {
        super(bytes);

        uuid = readUUID();
        message = readBungeeText();
    }

    @Override
    public void write() {
        writeUUID(uuid);
        writeBungeeText(message);
    }

    public PacketRequestMessageSend(String targetProxy, UUID uuid, BaseComponent[] message) {
        super(targetProxy);

        this.uuid = uuid;
        this.message = message;

        write();
    }
}
