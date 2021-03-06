package me.illusion.skyblockcore.shared.data;

import lombok.Getter;
import lombok.Setter;
import me.illusion.skyblockcore.spigot.sql.serialized.SerializedLocation;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class PlayerData implements Serializable {

    private UUID islandId;
    private double money = 0;
    private float experience = 0;
    private int experienceLevel = 0;
    private SerializedLocation islandLocation = new SerializedLocation();
    private SerializedLocation lastLocation = new SerializedLocation();

}
