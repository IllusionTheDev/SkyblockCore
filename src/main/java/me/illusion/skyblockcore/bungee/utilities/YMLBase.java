package me.illusion.skyblockcore.bungee.utilities;

import lombok.Getter;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Getter
public class YMLBase {

    private final File file;
    private Configuration configuration;

    public YMLBase(Plugin plugin, String name) {
        this(plugin, new File(plugin.getDataFolder(), name), true);
    }

    public YMLBase(Plugin plugin, File file, boolean existsOnSource) {
        this.file = file;

        if (!file.exists()) {
            try (InputStream stream = plugin.getResourceAsStream(file.getName())) {
                file.getParentFile().mkdirs();
                if (existsOnSource) {
                    Files.copy(stream, file.toPath());
                    stream.close();
                } else
                    file.createNewFile();
            } catch (IOException e) {
                ExceptionLogger.log(e);
            }


        }

        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            ExceptionLogger.log(e);
        }
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            ExceptionLogger.log(e);
        }
    }
}
