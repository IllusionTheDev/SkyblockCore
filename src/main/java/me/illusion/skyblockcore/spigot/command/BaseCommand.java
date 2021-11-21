package me.illusion.skyblockcore.spigot.command;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResultFull;
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
        System.out.println("tab completing this shit");

        if (args.length == 0)
            return Collections.emptyList();

        String identifier = String.join(".", name, String.join(".", args));

        return main.getCommandManager().tabComplete(identifier);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
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

        ComparisonResultFull fullComparison = main.getCommandManager().fullComparison(identifier);
        SkyblockCommand command1 = main.getCommandManager().get(identifier);

        List<Integer> wildcards = fullComparison.getWildCards();

        String[] commandArgs = new String[wildcards.size()];

        for (int index = 0; index < wildcards.size(); index++)
            commandArgs[index] = args[wildcards.get(index) - 1];

        command1.execute(sender, commandArgs);
        return true;
    }


}