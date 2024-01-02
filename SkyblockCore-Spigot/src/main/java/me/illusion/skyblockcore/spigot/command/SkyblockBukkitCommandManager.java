package me.illusion.skyblockcore.spigot.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.manager.AbstractSkyblockCommandManager;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.player.audience.SkyblockBukkitConsoleAudience;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkyblockBukkitCommandManager extends AbstractSkyblockCommandManager {

    private static final CommandMap COMMAND_MAP;
    private static final Constructor<PluginCommand> PLUGIN_COMMAND_CONSTRUCTOR;

    private static final Method REGISTER_SERVER_ALIASES_METHOD;
    private static final Method SYNC_COMMANDS_METHOD;
    private static final Method INITIALIZE_HELPMAP_COMMANDS;

    static {
        try {
            Class<?> serverClass = Bukkit.getServer().getClass();
            Field commandMapField = serverClass.getDeclaredField("commandMap");

            commandMapField.setAccessible(true);

            COMMAND_MAP = (CommandMap) commandMapField.get(Bukkit.getServer());

            Class<PluginCommand> pluginCommandClass = PluginCommand.class;

            PLUGIN_COMMAND_CONSTRUCTOR = pluginCommandClass.getDeclaredConstructor(String.class, Plugin.class);
            PLUGIN_COMMAND_CONSTRUCTOR.setAccessible(true); // Need to use the constructor as the class has a protected constructor and is final

            SYNC_COMMANDS_METHOD = serverClass.getDeclaredMethod("syncCommands");
            SYNC_COMMANDS_METHOD.setAccessible(true);

            REGISTER_SERVER_ALIASES_METHOD = commandMapField.getType().getSuperclass().getDeclaredMethod("registerServerAliases");
            REGISTER_SERVER_ALIASES_METHOD.setAccessible(true);

            Class<?> helpMapClass = Bukkit.getHelpMap().getClass();
            INITIALIZE_HELPMAP_COMMANDS = helpMapClass.getDeclaredMethod("initializeCommands");
            INITIALIZE_HELPMAP_COMMANDS.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final SkyblockSpigotPlugin platform;

    public SkyblockBukkitCommandManager(SkyblockSpigotPlugin platform) {
        super(platform);
        this.platform = platform;
    }

    @Override
    public void registerRoot(String name) {
        SkyblockBukkitCommand command = new SkyblockBukkitCommand();

        try {
            PluginCommand pluginCommand = PLUGIN_COMMAND_CONSTRUCTOR.newInstance(name, platform);

            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);

            COMMAND_MAP.register(name, pluginCommand);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void syncCommands() {
        try {
            REGISTER_SERVER_ALIASES_METHOD.invoke(COMMAND_MAP);
            INITIALIZE_HELPMAP_COMMANDS.invoke(Bukkit.getHelpMap());
            SYNC_COMMANDS_METHOD.invoke(Bukkit.getServer());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private class SkyblockBukkitCommand implements CommandExecutor, TabCompleter {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            if (sender instanceof ConsoleCommandSender) {
                handle(new SkyblockBukkitConsoleAudience(), s, strings);
                return true;
            }

            if (!(sender instanceof Player player)) {
                return true; // Not quite supported, should add support for this
            }

            SkyblockAudience audience = platform.getPlayerManager().getPlayer(player.getUniqueId());
            handle(audience, s, strings);
            return true;
        }

        @Nullable
        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            if (sender instanceof ConsoleCommandSender) {
                return tabComplete(new SkyblockBukkitConsoleAudience(), s, strings);
            }

            if (!(sender instanceof Player player)) {
                return null; // Not quite supported, should add support for this
            }

            SkyblockAudience audience = platform.getPlayerManager().getPlayer(player.getUniqueId());
            return tabComplete(audience, s, strings);
        }
    }
}
