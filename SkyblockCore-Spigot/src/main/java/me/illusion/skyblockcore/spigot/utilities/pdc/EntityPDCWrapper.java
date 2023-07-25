package me.illusion.skyblockcore.spigot.utilities.pdc;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A wrapper for an entity's persistent data container, providing simpler set... and get... methods
 */
public class EntityPDCWrapper extends PDCWrapper {

    public EntityPDCWrapper(JavaPlugin plugin, Entity entity) {
        super(plugin, entity.getPersistentDataContainer());
    }

}
