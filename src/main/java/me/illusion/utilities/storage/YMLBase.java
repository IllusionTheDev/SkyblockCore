package me.illusion.utilities.storage;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class YMLBase {

    private final boolean existsOnSource;
    private final JavaPlugin plugin;

    protected File file;

    @Getter
    private final FileConfiguration configuration;

    public YMLBase(JavaPlugin plugin, String name) {
        this(plugin, new File(plugin.getDataFolder(), name), true);
    }

    public YMLBase(JavaPlugin plugin, File file, boolean existsOnSource) {
        this.plugin = plugin;
        this.file = file;
        this.existsOnSource = existsOnSource;

        this.configuration = loadConfiguration();
    }

    protected void save()
    {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileConfiguration loadConfiguration() {
        FileConfiguration cfg = new YamlConfiguration();

        CompletableFuture.runAsync(() -> {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (existsOnSource)
                    plugin.saveResource(file.getAbsolutePath().replace(plugin.getDataFolder().getAbsolutePath() + "/", ""), false);
                else {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });


        return cfg;
    }
}