package me.illusion.skyblockcore.shared.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;

import java.util.UUID;

@Getter
@Setter
@ToString
public class PlayerData implements SkyblockSerializable {

    private UUID playerId;

    private UUID islandId;

    private SerializedLocation islandLocation = new SerializedLocation();
    private SerializedLocation lastLocation = new SerializedLocation();

}
