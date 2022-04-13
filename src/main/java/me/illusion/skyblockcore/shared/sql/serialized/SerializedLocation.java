package me.illusion.skyblockcore.shared.sql.serialized;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.utilities.BukkitConverter;
import org.bukkit.Location;

import java.io.Serializable;

@Getter
public class SerializedLocation implements Serializable {

    private String format;


    /**
     * Obtains the Bukkit location
     *
     * @return the bukkit location
     */
    public Location getLocation() {
        return BukkitConverter.convertLocation(this);
    }


    public void update(Location location) {
        update(location, location.getWorld().getName());
    }

    /**
     * Updates its internal location
     *
     * @param location - The new location to update to
     */
    public void update(Location location, String world) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        this.format = x + " " + y + " " + z + " " + yaw + " " + pitch + " " + world; // Not using String.format because it is awfully slow
    }

    @Override
    public String toString() {
        return format;
    }

    public String getWorldName() {
        return format.split(" ")[5];
    }
}
