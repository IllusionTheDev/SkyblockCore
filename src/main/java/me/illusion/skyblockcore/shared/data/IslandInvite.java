package me.illusion.skyblockcore.shared.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class IslandInvite {

    private final UUID inviteId;
    private final UUID sender;
    private final String target;
}
