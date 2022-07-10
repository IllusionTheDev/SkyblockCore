package me.illusion.skyblockcore.spigot.island.impl;

import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketTeleportPlayerToIsland;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class RemoteIsland implements Island {

    private final SkyblockPlugin main;
    private final IslandData islandData;

    public RemoteIsland(SkyblockPlugin main, IslandData islandData) {
        this.main = main;
        this.islandData = islandData;
    }

    @Override
    public CompletableFuture<Void> save() {
        return null;
    }

    @Override
    public CompletableFuture<Void> saveData() {
        return null; // Let's not dive here for now, de-syncing is not a good thing
    }

    @Override
    public void teleport(Player player) {
        main.getIslandManager().getIslandData(player.getUniqueId())
                .thenAccept((islandData) -> {
                    main.getPacketManager().send(new PacketTeleportPlayerToIsland(player.getUniqueId(), islandData, this.islandData.getId()));
                });
    }

    @Override
    public String getWorldName() {
        return null; // This is a remote island, basically a reference to an island loaded on another server
    }

    @Override
    public IslandData getData() {
        return islandData;
    }

    @Override
    public boolean locationBelongs(Location location) {
        return false;
    }
}
