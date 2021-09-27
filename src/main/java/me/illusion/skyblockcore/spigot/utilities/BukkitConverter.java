package me.illusion.skyblockcore.spigot.utilities;

import me.illusion.skyblockcore.shared.utilities.StringUtil;
import me.illusion.skyblockcore.spigot.sql.serialized.SerializedLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitConverter {

    public static Location convertLocation(SerializedLocation location) {
        String format = location.getFormat();

        if (format == null || format.isEmpty())
            return null;

        String[] split = StringUtil.split(format, ' '); // Faster split method

        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        double z = Double.parseDouble(split[2]);
        float yaw = Float.parseFloat(split[3]);
        float pitch = Float.parseFloat(split[4]);
        String world = split[5];
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
