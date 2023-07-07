package me.illusion.skyblockcore.common.data;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IslandData {

    private final UUID islandId;
    private final UUID ownerId;

}
