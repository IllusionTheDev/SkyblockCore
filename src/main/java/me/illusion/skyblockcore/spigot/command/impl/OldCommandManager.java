package me.illusion.skyblockcore.spigot.command.impl;

import me.illusion.skyblockcore.shared.utilities.StringUtil;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.BaseCommand;
import me.illusion.skyblockcore.spigot.command.CommandManager;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResult;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResultFull;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class OldCommandManager implements CommandManager {

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

    private final Set<String> registeredBaseCommands = new HashSet<>();
    private final Map<String, SkyblockCommand> commands = new HashMap<>();
    private final SkyblockPlugin main;

    public OldCommandManager(SkyblockPlugin main) {
        this.main = main;
    }

    public void runTests() {
        System.out.println("Running tests: ");

        System.out.println("Testing full comparison - island -> " + get("island"));
        System.out.println("Testing full comparison - island.go -> " + get("island.go"));

        System.out.println("Testing tab complete - is -> " + Arrays.toString(tabComplete("is").toArray(new String[0])));
        System.out.println("Testing tab complete - isl -> " + Arrays.toString(tabComplete("isl").toArray(new String[0])));
        System.out.println("Testing tab complete - island -> " + Arrays.toString(tabComplete("island").toArray(new String[0])));
    }

    public void register(SkyblockCommand command) {
        System.out.println("Registered command " + command.getClass().getSimpleName());

        commands.put(command.getIdentifier(), command);

        String base = getBaseCommand(command.getIdentifier());

        System.out.println(command.getIdentifier() + "'s base command is " + base);

        if (!registeredBaseCommands.contains(base)) {
            System.out.println(base + " was not registered as a command, registering..");

            try {
                PluginCommand pluginCommand = pluginCommandConstructor.newInstance(base, main);

                BaseCommand baseCommand = new BaseCommand(main);

                pluginCommand.setExecutor(baseCommand);
                pluginCommand.setTabCompleter(baseCommand);

                commandMap.register(base, pluginCommand);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            System.out.println(base + " was registered as a command");
            registeredBaseCommands.add(base);
        }
    }

    public ComparisonResultFull fullComparison(String identifier) {
        ComparisonResultFull full = new ComparisonResultFull(commands);
        full.match(identifier);
        return full;
    }

    public SkyblockCommand get(String identifier) {
        ComparisonResultFull full = new ComparisonResultFull(commands);
        return full.match(identifier);
    }

    public List<String> tabComplete(String identifier) {
        ComparisonResult result = new ComparisonResult(commands);
        List<String> results = result.match(identifier);

        // Extra processing
        int length = StringUtil.split(identifier, '.').length;
        List<String> toReturn = new ArrayList<>();

        for (String str : results) {
            String[] split = StringUtil.split(str, '.');

            String[] newArray = new String[split.length - length];

            if (split.length - length >= 0)
                System.arraycopy(split, length, newArray, 0, split.length - length);

            toReturn.add(String.join(".", newArray));
        }

        return toReturn;
    }

    public SkyblockCommand get(String name, String... args) {
        String identifier = String.join(".", name, String.join(".", args));

        return get(identifier);
    }

    private String getBaseCommand(String identifier) {
        int index = identifier.indexOf(".");
        return index == -1 ? identifier : identifier.substring(0, index);
    }

}