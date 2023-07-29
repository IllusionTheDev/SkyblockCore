package me.illusion.skyblockcore.bungee.utilities.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeYMLBase {

    private final boolean existsOnSource;
    private final Plugin plugin;

    protected File file;
    private Configuration configuration;

    public BungeeYMLBase(Plugin plugin, String name) {
        this(plugin, new File(plugin.getDataFolder(), name), true);
    }

    public BungeeYMLBase(Plugin plugin, File file, boolean existsOnSource) {
        this.plugin = plugin;
        this.file = file;
        this.existsOnSource = existsOnSource;
        this.configuration = this.loadConfiguration();
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Configuration loadConfiguration() {
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            if (this.existsOnSource) {
                saveResource();
            } else {
                try {
                    this.file.createNewFile();
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
            file.createNewFile();
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(input, this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeUnsetValues() {
        if (!this.existsOnSource) {
            return;
        }

        String inputName = this.file.getAbsolutePath().replace(this.plugin.getDataFolder().getAbsolutePath() + File.separator, "");

        try (InputStreamReader reader = new InputStreamReader(this.plugin.getResourceAsStream(inputName))) {
            Configuration input = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);

            for (String key : input.getKeys()) {
                if (!this.configuration.contains(key)) {
                    this.configuration.set(key, input.get(key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void reload() {
        this.configuration = this.loadConfiguration();
    }

    public File getFile() {
        return this.file;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
}
