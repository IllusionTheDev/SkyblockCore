package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.skyblockcore.common.communication.packet.PacketManager;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SkyblockSpigotPlugin extends JavaPlugin {

    private PacketManager packetManager;

    private SkyblockCosmosSetup cosmosSetup;
    private SkyblockDatabase database;

    private IslandManager islandManager;

    @Override
    public void onEnable() {

    }

    public void initPackets() {
        packetManager = new PacketManager();
    }
}
