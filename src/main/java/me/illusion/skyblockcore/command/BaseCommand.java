package me.illusion.skyblockcore.command;

import me.illusion.skyblockcore.CorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class BaseCommand extends BukkitCommand {

    private final SkyblockCommand command;
    private final CorePlugin main;

    protected BaseCommand(String name, CorePlugin main, SkyblockCommand command) {
        super(name);
        this.command = command;
        this.main = main;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        return false;
    }
}
