package me.illusion.skyblockcore.spigot.utilities.pdc;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityPDCWrapper extends PDCWrapper {

    public EntityPDCWrapper(JavaPlugin plugin, Entity entity) {
        super(plugin, entity.getPersistentDataContainer());
    }

}
