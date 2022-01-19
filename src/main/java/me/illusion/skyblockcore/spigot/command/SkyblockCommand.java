package me.illusion.skyblockcore.spigot.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public interface SkyblockCommand {

    /**
     * The command identifier, use a . to separate subcommands
     * Example: island.teleport
     *
     * @return identifier
     */
    String getIdentifier();

    /**
     * The command aliases
     *
     * @return NULL if no aliases are present
     */
    default String[] getAliases() {
        return new String[0];
    }

    /**
     * The permission to execute the command
     *
     * @return EMPTY if no permission is present
     */
    default String getPermission() {
        return "";
    }

    /**
     * This is called after the permission checks
     *
     * @param sender - The command sender
     * @return TRUE if the sender can execute the command, FALSE otherwise
     */
    default boolean canExecute(CommandSender sender) {
        return true;
    }

    default boolean hasPermission() {
        return getPermission().isEmpty();
    }

    /**
     * Executes the command
     *
     * @param sender - The command sender
     * @param args   - The arguments external to the identifier
     */
    void execute(CommandSender sender, String... args);

    default List<Integer> getWildcards() {
        String identifier = getIdentifier();
        List<Integer> wildcards = new ArrayList<>();

        String[] split = identifier.split("\\.");

        for (int index = 0; index < split.length; index++) {
            if (split[index].equals("*")) {
                wildcards.add(index);
            }
        }

        return wildcards;
    }
}
