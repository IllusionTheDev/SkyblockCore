package me.illusion.skyblockcore.spigot.command.island.movement;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import org.bukkit.command.CommandSender;

public class IslandVisitCommand implements SkyblockCommand {

    private final SkyblockPlugin main;

    public IslandVisitCommand(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public String getIdentifier() {
        return "island.visit.*";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        String targetPlayer = args[0];

        if (sender.getName().equalsIgnoreCase(targetPlayer)) {
            main.getMessages().sendMessage(sender, "command.visit-self");
            return;
        }


    }

}
