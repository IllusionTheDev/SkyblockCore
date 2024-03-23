package me.illusion.skyblockcore.common.packet.channel;

import java.util.ArrayList;
import java.util.List;

public class MultiPacketChannel implements PacketChannel {

    private final PacketChannel[] channels;

    public MultiPacketChannel(PacketChannel... channels) {
        this.channels = channels;
    }

    @Override
    public Iterable<String> getChannels() {
        List<String> list = new ArrayList<>(channels.length);
        for (PacketChannel channel : channels) {
            list.addAll((List<String>) channel.getChannels());
        }
        return list;
    }
}
