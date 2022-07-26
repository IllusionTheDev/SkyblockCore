package me.illusion.skyblockcore.shared.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class IslandInvite {

    private final UUID inviteId;
    private final UUID sender;
    private final String senderName;

    private final UUID target;
    private final String targetName;
    private final long expirationEpoch;
}
