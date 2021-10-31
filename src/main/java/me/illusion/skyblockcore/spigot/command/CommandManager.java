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

    private final Map<String, SkyblockCommand> commands = new HashMap<>();
    private final SkyblockPlugin main;

    public CommandManager(SkyblockPlugin main) {
        this.main = main;

        System.out.println("Running tests: ");
        System.out.println("island.go -> " + get(true, "island.go"));
        System.out.println("island -> " + get(true, "island"));
        System.out.println("is (partial match) -> " + get(false, "is"));
        System.out.println("island.invite -> " + get(false, "island.invite"));
        System.out.println("island.invite.ImIllusion -> " + get(true, "island.invite.ImIllusion"));
    }

    public void register(SkyblockCommand command) {
        System.out.println("Registered command " + command.getClass().getSimpleName());

        commands.put(command.getIdentifier(), command);
    }

    public SkyblockCommand get(boolean fullMatch, String identifier) {
        for (Map.Entry<String, SkyblockCommand> entry : commands.entrySet()) {
            ComparisonResult result = new ComparisonResult(identifier, entry.getKey(), entry.getValue().getAliases());

            if ((fullMatch && result.isFullyMatches()) || (!fullMatch && result.isPartiallyMatches()))
                return entry.getValue();
        }

        return null;
    }

    public SkyblockCommand get(boolean fullMatch, String name, String... args) {
        String identifier = String.join(".", name, String.join(".", args));

        return get(fullMatch, identifier);
    }

    private String getBaseCommand(String identifier) {
        int index = identifier.indexOf(".");
        return index == -1 ? identifier : identifier.substring(0, index);
    }
}
