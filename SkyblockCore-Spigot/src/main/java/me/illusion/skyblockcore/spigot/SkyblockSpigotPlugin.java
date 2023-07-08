package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.cosmos.utilities.command.command.CommandManager;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkRegistry;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SkyblockSpigotPlugin extends JavaPlugin {

    // General things (usually present in Cosmos)
    private CommandManager commandManager;
    private MessagesFile messages;

    // Skyblock-specific setup
    private SkyblockCosmosSetup cosmosSetup;
    private SkyblockDatabase database;

    private IslandManager islandManager;

    private SkyblockNetworkRegistry networkRegistry;

    @Override
    public void onEnable() {
        messages = new MessagesFile(this);
        commandManager = new CommandManager(this, messages);
    }

}
