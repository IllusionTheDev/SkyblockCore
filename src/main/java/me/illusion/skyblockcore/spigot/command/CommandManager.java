package me.illusion.skyblockcore.spigot.command;

import me.illusion.skyblockcore.shared.utilities.StringUtil;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResult;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResultFull;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.*;

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

    private final Set<String> registeredBaseCommands = new HashSet<>();
    private final Map<String, SkyblockCommand> commands = new HashMap<>();
    private final SkyblockPlugin main;

    public CommandManager(SkyblockPlugin main) {
        this.main = main;

        System.out.println("Running tests: ");

    }

    public void register(SkyblockCommand command) {
        System.out.println("Registered command " + command.getClass().getSimpleName());

        commands.put(command.getIdentifier(), command);

        String base = getBaseCommand(command.getIdentifier());
        if (!registeredBaseCommands.contains(base)) {
            commandMap.register(base, new BaseCommand(base, main));
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
