package me.illusion.skyblockcore.spigot.island.invite;

import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InviteCache {

    private final Map<UUID, IslandInvite> invites = new ConcurrentHashMap<>();

    public InviteCache(SkyblockPlugin main) {
        new ScheduleBuilder(main)
                .every(1).seconds()
                .run(() -> {
                    for (Map.Entry<UUID, IslandInvite> entry : invites.entrySet()) {
                        IslandInvite invite = entry.getValue();
                        if (invite.getExpirationEpoch() < Instant.now().getEpochSecond()) {
                            invites.remove(entry.getKey());
                        }
                    }
                }).sync()
                .start();
    }

    public void addInvite(IslandInvite invite) {
        invites.put(invite.getInviteId(), invite);
    }

    public void removeInvite(UUID inviteId) {
        invites.remove(inviteId);
    }

    public void removeInvite(IslandInvite invite) {
        invites.remove(invite.getInviteId());
    }

    public IslandInvite getInvite(UUID inviteId) {
        return invites.get(inviteId);
    }

    public IslandInvite getInvite(Player target, String senderName) {
        for (IslandInvite invite : invites.values()) {
            if (invite.getTarget().equals(target.getUniqueId()) && invite.getSenderName().equals(senderName)) {
                return invite;
            }
        }
        return null;
    }

    public IslandInvite getInvite(String target) {
        for (IslandInvite invite : invites.values())
            if (invite.getTargetName().equals(target)) {
                return invite;
            }

        return null;
    }

}
