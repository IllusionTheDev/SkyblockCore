package me.illusion.skyblockcore.common.data;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IslandData implements Serializable {

    private final UUID islandId;
    private final UUID ownerId;

    // TODO: Add more data here

}
