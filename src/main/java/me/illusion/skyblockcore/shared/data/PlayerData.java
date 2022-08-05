package me.illusion.skyblockcore.shared.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PlayerData implements SkyblockSerializable {

    private UUID playerId;

    private UUID islandId;

    private SerializedLocation islandLocation = new SerializedLocation();
    private SerializedLocation lastLocation = new SerializedLocation();

    @Override
    public void load(Map<String, Object> map) {
        playerId = UUID.fromString(map.get("playerId").toString());
        islandId = UUID.fromString(map.get("islandId").toString());

        String islandLocationString = map.get("islandLocation").toString();
        String lastLocationString = map.get("lastLocation").toString();

        islandLocation.setFormat(islandLocationString);
        lastLocation.setFormat(lastLocationString);
    }

    @Override
    public void save(Map<String, Object> map) {
        map.put("playerId", playerId.toString());
        map.put("islandId", islandId.toString());

        map.put("islandLocation", islandLocation.getFormat());
        map.put("lastLocation", lastLocation.getFormat());

    }
}
