package me.illusion.skyblockcore.data;

import lombok.Getter;
import lombok.Setter;
import me.illusion.skyblockcore.sql.serialized.SerializedItemStackArray;
import me.illusion.skyblockcore.sql.serialized.SerializedLocation;

import java.io.File;
import java.io.Serializable;

@Getter
@Setter
public class PlayerData implements Serializable {

    private File islandSchematic = null;
    private double money = 0;
    private SerializedItemStackArray inventory = new SerializedItemStackArray();
    private SerializedLocation islandLocation = new SerializedLocation();
    private SerializedLocation lastLocation = new SerializedLocation();

}
