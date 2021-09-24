package me.illusion.skyblockcore.shared.packet.event;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;

@Getter
public class PacketReceiveEvent {

    private final Packet packet;

    public PacketReceiveEvent(Packet packet) {
        this.packet = packet;
    }
}
