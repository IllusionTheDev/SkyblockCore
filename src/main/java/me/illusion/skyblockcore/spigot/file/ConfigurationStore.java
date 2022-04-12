package me.illusion.skyblockcore.spigot.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.illusion.skyblockcore.spigot.utilities.storage.MessagesFile;

@Getter
@AllArgsConstructor
public class ConfigurationStore {

    /*
        Message file, used to send and obtain messages
     */
    private MessagesFile messages;

    /*
        Settings file, contains essential info that are crucial for the plugin,
        such as world anti-corruption delays and database information
     */
    private SettingsFile settings;

    private IslandConfig islandConfig;
}
