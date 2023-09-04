package me.illusion.skyblockcore.common.command.context;

/**
 * Represents a command context. This allows for the retrieval of arguments and is passed for tab completion and command execution.
 */
public interface CommandContext {

    /**
     * Gets an argument by name
     *
     * @param name The name of the argument.
     * @param <T>  The type of the argument.
     * @return The parsed argument.
     */
    <T> T getArgument(String name); // I know it's an unsafe cast, but Cloud commands do it too, so I'm not going to bother.

    /**
     * Gets an argument by index. Starts at 0.
     * @param index The index of the argument.
     * @return The parsed argument.
     * @param <T> The type of the argument.
     */
    <T> T getArgument(int index);

    /**
     * Gets the full input of the command. This is the full command, not just the arguments, without a /
     * @return The full input.
     */
    String getFullInput();

    /**
     * Gets the last input of the command. This is the last word that was parsed by the last argument.
     * @return The last input.
     */
    String getLastInput();
}
