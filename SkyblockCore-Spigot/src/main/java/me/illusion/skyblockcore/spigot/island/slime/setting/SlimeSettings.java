package me.illusion.skyblockcore.spigot.island.slime.setting;

import lombok.Getter;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;

@Getter
public class SlimeSettings {

    private final String preferredLoader;
    private final double radius;


    public SlimeSettings(ConfigurationSection section) {
        this.preferredLoader = section.getString("preferred-loader");
        this.radius = section.getDouble("radius");
    }
}
