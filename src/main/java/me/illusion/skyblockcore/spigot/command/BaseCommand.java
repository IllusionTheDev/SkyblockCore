package me.illusion.skyblockcore.spigot.command;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class BaseCommand implements CommandExecutor, TabCompleter {

    private final SkyblockPlugin main;

    public BaseCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String name, String[] args) throws IllegalArgumentException {
        if (args.length == 0)
            return Collections.emptyList();

        String identifier = String.join(".", name, String.join(".", args)).replace(" ", ".");
        return main.getCommandManager().tabComplete(identifier);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
        String identifier = String.join(".", name, String.join(".", args)).replace(" ", ".");

        // remove trailing dots, this is not tab completion
        while (identifier.endsWith(".")) {
            identifier = identifier.substring(0, identifier.length() - 1);
        }

        SkyblockCommand command = main.getCommandManager().get(identifier);

        if (command == null) {
            main.getFiles().getMessages().sendMessage(sender, "invalid-args");
            return true;
        }

        String permission = command.getPermission();

        if (!command.canExecute(sender)) {
            main.getFiles().getMessages().sendMessage(sender, "command.cannot-use", (str) -> str.replace("%permission%", permission).replace("%command%", name));
            return true;
        }

        if (command.hasPermission() && !sender.hasPermission(command.getPermission())) {
            main.getFiles().getMessages().sendMessage(sender, "command.no-permission", (str) -> str.replace("%permission%", permission).replace("%command%", name));
            return true;
        }


        List<Integer> wildcards = command.getWildcards();

        String[] commandArgs = new String[wildcards.size()];

        for (int index = 0; index < wildcards.size(); index++)
            commandArgs[index] = args[wildcards.get(index) - 1];

        command.execute(sender, commandArgs);
        return true;
    }


}