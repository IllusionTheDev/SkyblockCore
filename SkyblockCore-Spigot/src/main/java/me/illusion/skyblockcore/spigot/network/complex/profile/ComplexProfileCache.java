package me.illusion.skyblockcore.spigot.network.complex.profile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.communication.packet.Packet;
import me.illusion.skyblockcore.common.communication.packet.PacketManager;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.announce.PacketAnnounceProfileUpdate;
import me.illusion.skyblockcore.spigot.network.simple.profile.SimpleProfileCache;

/**
 * The complex profile cache is a slight variation from the simple player cache, sending extra packets to update other servers when a player switches profiles.
 */
public class ComplexProfileCache extends SimpleProfileCache {

    private final PacketManager packetManager;

    public ComplexProfileCache(ComplexSkyblockNetwork network) {
        super(network.getPlugin());

        this.packetManager = network.getCommunicationsHandler().getPacketManager();

        packetManager.subscribe(PacketAnnounceProfileUpdate.class, this::handleProfileUpdate);
    }

    @Override
    public CompletableFuture<Void> saveProfileId(UUID playerId, UUID newProfileId) {
        UUID oldProfileId = getCachedProfileId(playerId);

        if (oldProfileId == null) {
            return super.saveProfileId(playerId, newProfileId);
        }

        return super.saveProfileId(playerId, newProfileId).thenCompose(irrelevant -> {
            Packet packet = new PacketAnnounceProfileUpdate(playerId, oldProfileId, newProfileId);
            return packetManager.send(packet);
        });
    }

    /**
     * This method is used to handle a profile update packet.
     *
     * @param packet The packet to handle.
     */
    private void handleProfileUpdate(PacketAnnounceProfileUpdate packet) {
        UUID playerId = packet.getPlayerId();
        UUID oldProfileId = packet.getOldProfileId();
        UUID newProfileId = packet.getNewProfileId();

        UUID cachedId = getCachedProfileId(playerId);

        if (oldProfileId.equals(cachedId)) { // cachedId will be null if the player is not cached, so this will not be executed if the player is not cached
            setIdInternally(playerId, newProfileId);
        }
    }

}
