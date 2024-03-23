package me.illusion.skyblockcore.common.packet.channel;

public final class PacketChannels {

    public static final IndividualPacketChannel GAME_SERVICE = PacketChannel.individual("game_service");
    public static final PacketChannel ALL_INSTANCES = PacketChannel.individual("all_instances");
    public static final PacketChannel ALL_LOBBIES = PacketChannel.individual("all_lobbies");
    public static final PacketChannel ALL_PROXIES = PacketChannel.individual("all_proxies");
    public static final PacketChannel ALL = PacketChannel.individual("all");

    private PacketChannels() {
    }

}
