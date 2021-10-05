package me.illusion.skyblockcore.spigot.command.island.invite;

import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandInviteCommand implements SkyblockCommand {

    @Override
    public String getIdentifier() {
        return "island.invite";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSender sender, String... args) {

    }
}
