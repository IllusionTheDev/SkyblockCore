package me.illusion.skyblockcore.spigot.player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.server.inventory.PlayerItemContainer;
import me.illusion.skyblockcore.server.player.AbstractSkyblockPlayer;
import me.illusion.skyblockcore.server.player.SkyblockPlayerManager;
import me.illusion.skyblockcore.server.util.SkyblockLocation;
import me.illusion.skyblockcore.spigot.inventory.container.BukkitPlayerItemContainer;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Adapter class for {@link me.illusion.skyblockcore.server.player.SkyblockPlayer} for Bukkit.
 */
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

    @Override
    public PlayerItemContainer getInventory() {
        verifyOnline();
        return BukkitPlayerItemContainer.create(getBukkitPlayer().getInventory());
    }

    private void verifyOnline() {
        if (getBukkitPlayer() == null) {
            throw new IllegalStateException("Player is not online!");
        }
    }

    private Player getBukkitPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    @Override
    public void sendMessage(String message) {
        verifyOnline();
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        verifyOnline();
        return getBukkitPlayer().hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return false;
    }
}
