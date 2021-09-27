package me.illusion.skyblockcore.spigot.command;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResult;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private static CommandMap commandMap;

    static {
        try {
            Server server = Bukkit.getServer();

            Field commandMapField = server.getClass().getDeclaredField("commandMap");

            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(server);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private final Map<String, BaseCommand> baseCommands = new HashMap<>();
    private final Map<String, SkyblockCommand> commands = new HashMap<>();
    private final SkyblockPlugin main;

    public CommandManager(SkyblockPlugin main) {
        this.main = main;
    }

    private void makeCommand(SkyblockCommand command) {
        String identifier = command.getIdentifier();
        String name = getBaseCommand(identifier);

        BaseCommand baseCommand = baseCommands.getOrDefault(name, null);

        if (baseCommand == null) {
            baseCommand = new BaseCommand(name, main);
            baseCommands.put(name, baseCommand);
            commandMap.register(name, baseCommand);
        }

        baseCommand.registerCommand(command);
    }

    public void register(SkyblockCommand command) {
        System.out.println("Registered command " + command.getClass().getSimpleName());

        commands.put(command.getIdentifier(), command);
        makeCommand(command);
    }

    public SkyblockCommand get(String identifier) {
        for (Map.Entry<String, SkyblockCommand> entry : commands.entrySet()) {
            ComparisonResult result = new ComparisonResult(identifier, entry.getKey(), entry.getValue().getAliases());

            if (result.isFullyMatches())
                return entry.getValue();
        }

        return null;
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
