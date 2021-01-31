package me.illusion.skyblockcore.bungee.command;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SkyblockCommand extends Command {

    private final SkyblockBungeePlugin main;

    public SkyblockCommand(SkyblockBungeePlugin main) {
        super("skyblock");
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
