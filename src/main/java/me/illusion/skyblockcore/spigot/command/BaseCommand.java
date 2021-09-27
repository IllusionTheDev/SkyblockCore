package me.illusion.skyblockcore.spigot.command;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResult;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.*;

public class BaseCommand extends BukkitCommand {

    private final SkyblockPlugin main;
    private final Map<String, SkyblockCommand> commands = new HashMap<>();

    protected BaseCommand(String name, SkyblockPlugin main) {
        super(name);
        this.main = main;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String name, String[] args) throws IllegalArgumentException {
        List<String> list = new ArrayList<>();

        if (args.length == 0)
            return Collections.emptyList();

        String identifier = String.join(".", name, String.join(".", args));

        for (Map.Entry<String, SkyblockCommand> entry : commands.entrySet()) {
            ComparisonResult result = new ComparisonResult(identifier, entry.getKey(), entry.getValue().getAliases());

            if (result.isPartiallyMatches())
                list.add(args[args.length - 1]);
        }

        return list;
    }

    @Override
    public boolean execute(CommandSender sender, String name, String[] args) {
        String identifier = String.join(".", name, String.join(".", args));

        SkyblockCommand command = main.getCommandManager().get(identifier);

        if (command == null) {
            main.getMessages().sendMessage(sender, "invalid-args");
            return true;
        }

        String permission = command.getPermission();

        if (!command.canExecute(sender)) {
            main.getMessages().sendMessage(sender, "command.cannot-use", (str) -> str.replace("%permission%", permission).replace("%command%", name));
            return true;
        }

        if (command.hasPermission() && !sender.hasPermission(command.getPermission())) {
            main.getMessages().sendMessage(sender, "command.no-permission", (str) -> str.replace("%permission%", permission).replace("%command%", name));
            return true;
        }

        ComparisonResult result = new ComparisonResult(identifier, command.getIdentifier(), command.getAliases());
        int[] wildcards = result.getWildcardPositions();
        int length = wildcards.length;
        String[] cmdArgs = new String[length];

        for (int i = 0; i < length; i++)
            cmdArgs[i] = args[wildcards[i]];

        command.execute(sender, cmdArgs);
        return true;
    }


    public void registerCommand(SkyblockCommand command) {
        commands.put(command.getIdentifier(), command);
    }
}
