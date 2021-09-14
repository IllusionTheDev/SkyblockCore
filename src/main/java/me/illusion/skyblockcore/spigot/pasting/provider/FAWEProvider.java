package me.illusion.skyblockcore.spigot.pasting.provider;

import org.bukkit.Location;

import java.io.File;

public interface FAWEProvider {

    void paste(File file, Location location);

    void save(File file, Location one, Location two);
}
