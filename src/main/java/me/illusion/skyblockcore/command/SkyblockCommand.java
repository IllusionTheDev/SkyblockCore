package me.illusion.skyblockcore.command;

import org.bukkit.command.CommandSender;

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
        return null;
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

    /**
     * Executes the command
     *
     * @param sender - The command sender
     * @param args   - The arguments external to the identifier
     */
    void execute(CommandSender sender, String... args);
}
