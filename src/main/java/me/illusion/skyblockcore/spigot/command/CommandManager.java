package me.illusion.skyblockcore.spigot.command;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResult;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResultFull;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
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

    }

    public void register(SkyblockCommand command) {
        System.out.println("Registered command " + command.getClass().getSimpleName());

        commands.put(command.getIdentifier(), command);
    }

    public SkyblockCommand get(String identifier) {
        ComparisonResultFull full = new ComparisonResultFull(commands);
        return full.match(identifier);
    }

    public List<String> tabComplete(String identifier) {
        ComparisonResult result = new ComparisonResult(commands);
        return result.match(identifier);
    }

    public SkyblockCommand get(String name, String... args) {
        String identifier = String.join(".", name, String.join(".", args));

        return get(identifier);
    }

}
