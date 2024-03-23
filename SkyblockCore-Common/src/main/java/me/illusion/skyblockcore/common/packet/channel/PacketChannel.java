package me.illusion.skyblockcore.common.packet.channel;

public interface PacketChannel {

    static IndividualPacketChannel individual(String channel) {
        return new IndividualPacketChannel(channel);
    }

    static PacketChannel multiple(PacketChannel... channels) {
        return new MultiPacketChannel(channels);
    }

    Iterable<String> getChannels();
}
