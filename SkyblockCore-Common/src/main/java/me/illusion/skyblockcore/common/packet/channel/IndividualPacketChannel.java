package me.illusion.skyblockcore.common.packet.channel;

import java.util.List;

public class IndividualPacketChannel implements PacketChannel {

    private final String channel;

    public IndividualPacketChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public Iterable<String> getChannels() {
        return List.of(channel);
    }
}
