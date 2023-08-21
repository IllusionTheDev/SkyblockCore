package me.illusion.skyblockcore.common.command.context;

public interface CommandContext {

    <T> T getArgument(String name); // I know it's an unsafe cast, but Cloud commands do it too, so I'm not going to bother.

    <T> T getArgument(int index);

    String getFullInput();

    String getLastInput();
}
