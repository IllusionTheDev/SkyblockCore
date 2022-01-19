package me.illusion.skyblockcore.spigot.command.island.information;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import org.bukkit.command.CommandSender;

public class IslandHelpCommand implements SkyblockCommand {

    private final SkyblockPlugin main;

    public IslandHelpCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public String getIdentifier() {
        return "island.help";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        main.getMessages().sendMessage(sender, "commands.island-help");
    }
}
