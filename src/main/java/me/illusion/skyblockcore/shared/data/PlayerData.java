package me.illusion.skyblockcore.shared.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PlayerData implements Serializable {

    private UUID playerId;

    private UUID islandId;

    private SerializedLocation islandLocation = new SerializedLocation();
    private SerializedLocation lastLocation = new SerializedLocation();

}
