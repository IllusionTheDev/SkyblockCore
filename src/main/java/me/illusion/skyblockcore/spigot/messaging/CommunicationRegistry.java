package me.illusion.skyblockcore.spigot.messaging;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import me.illusion.skyblockcore.shared.packet.PacketProcessor;
import me.illusion.skyblockcore.shared.packet.communication.CommunicationType;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class CommunicationRegistry {

    public static PacketProcessor getChosenProcessor(SkyblockPlugin main) {
        FileConfiguration config = main.getFiles().getSettings().getConfiguration();

        ConfigurationSection section = config.getConfigurationSection("communication");

        if (section == null) {
            return null;
        }

        Optional<CommunicationType> type = Enums.getIfPresent(CommunicationType.class, section.getString("type").toUpperCase(Locale.ENGLISH));

        if (!type.isPresent()) {
            ExceptionLogger.log(new IllegalArgumentException("Invalid communication type: " + section.getString("type")));
            return null;
        }

        CommunicationType communicationType = type.get();

        if (communicationType == CommunicationType.REDIS) {
            main.getDependencyDownloader().dependOn(
                    "redis.clients.Jedis",
                    "https://www.illusionthe.dev/dependencies/Skyblock.html",
                    "SkyblockDependencies.jar"
            );

            try {
                return new RedisMessaging(main);
            } catch (Exception e) {
                ExceptionLogger.log(e);
                return null;
            }
        }

        return null;
    }
}
