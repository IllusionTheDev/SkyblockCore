package me.illusion.skyblockcore.common.data;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class represents the internal data of an island. The data present in this class must be serializable, and all codecs in the Common module must be able
 * to serialize and deserialize this class. The data must also be universal, meaning that it must be able to be used by all server implementations, such as
 * Spigot, Bungee, Velocity etc.
 */
@Getter
@AllArgsConstructor
public class IslandData implements Serializable {

    private final UUID islandId;
    private final UUID ownerId; // Profile ID, not player

    // TODO: Add more data here

}
