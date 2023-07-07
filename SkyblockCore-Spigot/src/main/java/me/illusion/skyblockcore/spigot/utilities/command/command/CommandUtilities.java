package me.illusion.skyblockcore.spigot.utilities.command.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandUtilities {

    private static CommandMap commandMap;
    private static Constructor<PluginCommand> pluginCommandConstructor;

    static {
        try {
            Server server = Bukkit.getServer();
            Field commandMapField = server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(server);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            pluginCommandConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void registerCommand(String base, JavaPlugin plugin, BukkitBaseCommand baseCommand) {
        try {
            PluginCommand pluginCommand = pluginCommandConstructor.newInstance(base, plugin);

            pluginCommand.setExecutor(baseCommand);
            pluginCommand.setTabCompleter(baseCommand);

            commandMap.register(base, pluginCommand);

        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
