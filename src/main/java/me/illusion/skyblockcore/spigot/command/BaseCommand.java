package me.illusion.skyblockcore.spigot.command;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResult;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class BaseCommand extends BukkitCommand {

    private final SkyblockCommand command;
    private final SkyblockPlugin main;

    private final boolean hasPermission;
    private final String permission;

    protected BaseCommand(String name, SkyblockPlugin main, SkyblockCommand command) {
        super(name);
        this.command = command;
        this.main = main;

        this.permission = command.getPermission();
        hasPermission = !permission.equals("");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        String identifier = String.join(".", s, String.join(".", args));

        ComparisonResult result = compare(identifier);

        if(!result.isMatches())
            return true;

        if(!command.canExecute(sender)) {
            main.getMessages().sendMessage(sender, "command.cannot-use", (str) -> str.replace("%permission%", permission).replace("%command%", s));
            return true;
        }

        if(hasPermission && !sender.hasPermission(permission)) {
            main.getMessages().sendMessage(sender, "command.no-permission", (str) -> str.replace("%permission%", permission).replace("%command%", s));
            return true;
        }

        int[] wildcards = result.getWildcardPositions();
        int length = wildcards.length;
        String[] cmdArgs = new String[length];

        for(int i = 0; i < length; i++)
            cmdArgs[i] = args[wildcards[i]];

        command.execute(sender, cmdArgs);
        return true;
    }

    private ComparisonResult compare(String test) {
        return new ComparisonResult(command.getIdentifier(), test, command.getAliases());
    }
}
