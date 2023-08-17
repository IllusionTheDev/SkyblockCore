package me.illusion.skyblockcore.bungee.utilities.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Represents a YML wrapper for BungeeCord. Other than reading the configuration, this class also handles the copying of the default configuration file.
 */
public class BungeeYMLBase {

    private final boolean existsOnSource;
    private final Plugin plugin;

    protected File file;
    private final Configuration configuration;

    public BungeeYMLBase(Plugin plugin, File file, boolean existsOnSource) {
        this.plugin = plugin;
        this.file = file;
        this.existsOnSource = existsOnSource;
        this.configuration = this.loadConfiguration();
    }

    private Configuration loadConfiguration() {
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            if (this.existsOnSource) {
                saveResource();
            } else {
                try {
                    boolean success = this.file.createNewFile();
                    if (!success) {
                        throw new RuntimeException("Failed to create file " + this.file.getAbsolutePath());
                    }
                } catch (IOException var4) {
                    var4.printStackTrace();
                }
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    private void saveResource() {
        if (!this.existsOnSource) {
            throw new UnsupportedOperationException("This method is only supported when the file exists on the source");
        }

        if (file.exists()) {
            return;
        }

        File dataFolder = this.plugin.getDataFolder();
        String inputName = this.file.getAbsolutePath().replace(dataFolder.getAbsolutePath() + File.separator, "");

        try (InputStreamReader reader = new InputStreamReader(this.plugin.getResourceAsStream(inputName))) {
            Configuration input = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
            boolean success = file.createNewFile();

            if (!success) {
                throw new RuntimeException("Failed to create file " + this.file.getAbsolutePath());
            }

            ConfigurationProvider.getProvider(YamlConfiguration.class).save(input, this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
}
