package me.illusion.skyblockcore.spigot.player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.server.player.AbstractSkyblockPlayer;
import me.illusion.skyblockcore.server.player.SkyblockPlayerManager;
import me.illusion.skyblockcore.server.util.SkyblockLocation;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkyblockBukkitPlayer extends AbstractSkyblockPlayer {

    public SkyblockBukkitPlayer(SkyblockPlayerManager playerManager, UUID playerId) {
        super(playerManager, playerId);
    }

    @Override
    public String getName() {
        verifyOnline();
        return getBukkitPlayer().getName();
    }

    @Override
    public SkyblockLocation getLocation() {
        verifyOnline();
        return SkyblockBukkitAdapter.toSkyblockLocation(getBukkitPlayer().getLocation());
    }

    @Override
    public void teleport(SkyblockLocation location) {
        verifyOnline();

        if (!Bukkit.isPrimaryThread()) { // Prevents teleporting on the wrong thread
            CompletableFuture.runAsync(() -> teleport(location), MainThreadExecutor.INSTANCE);
            return;
        }

        getBukkitPlayer().teleport(SkyblockBukkitAdapter.toBukkitLocation(location));
    }

    private void verifyOnline() {
        if (getBukkitPlayer() == null) {
            throw new IllegalStateException("Player is not online!");
        }
    }

    private Player getBukkitPlayer() {
        return Bukkit.getPlayer(playerId);
    }
}
