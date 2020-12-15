package me.illusion.skyblockcore.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    private File islandSchematic = null;
    private double money = 0;
    private ItemStack[] inventory = null;
    private Location islandLocation;
    private Location lastLocation;

}
