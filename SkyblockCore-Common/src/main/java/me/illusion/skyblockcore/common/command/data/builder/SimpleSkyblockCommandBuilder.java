package me.illusion.skyblockcore.common.command.data.builder;

import java.util.LinkedList;
import java.util.List;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.context.arg.LiteralArgument;
import me.illusion.skyblockcore.common.command.data.SkyblockCommand;
import me.illusion.skyblockcore.common.command.data.SkyblockCommandBuilder;
import me.illusion.skyblockcore.common.command.data.SkyblockCommandHandler;
import me.illusion.skyblockcore.common.command.manager.AbstractSkyblockCommandManager;

/**
 * Represents a simple implementation of a SkyblockCommandBuilder.
 *
 * @param <T> The audience type.
 */
public class SimpleSkyblockCommandBuilder<T extends SkyblockAudience> implements SkyblockCommandBuilder<T> {

    private final List<CommandArgument> arguments;
    private final String name;
    private final Class<T> audienceClass;

    private final AbstractSkyblockCommandManager manager;

    private SkyblockCommandHandler<T> handler;
    private String permission;

    public SimpleSkyblockCommandBuilder(AbstractSkyblockCommandManager manager, String name, Class<T> audienceClass, List<CommandArgument> arguments) {
        this.manager = manager;
        this.name = name;
        this.audienceClass = audienceClass;
        this.arguments = arguments;

        arguments.add(new LiteralArgument(name));
    }

    public SimpleSkyblockCommandBuilder(AbstractSkyblockCommandManager manager, String name, Class<T> audienceClass) {
        this(
            manager,
            name,
            audienceClass,
            new LinkedList<>()
        );
    }

    @Override
    public SkyblockCommandBuilder<T> registerArgument(CommandArgument argument) {
        arguments.add(argument);
        return this;
    }

    @Override
    public SkyblockCommandBuilder<T> handler(SkyblockCommandHandler<T> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public SkyblockCommandBuilder<T> permission(String permission) {
        this.permission = permission;
        return this;
    }

    @Override
    public <V extends SkyblockAudience> SkyblockCommandBuilder<V> audience(Class<V> audience) {
        return new SimpleSkyblockCommandBuilder<>(manager, name, audience, arguments);
    }

    @Override
    public SkyblockCommand<T> build() {
        SkyblockCommand<T> command = new SimpleSkyblockCommand<>(arguments, handler, audienceClass, permission);
        manager.registerCommand(command);

        return command;
    }
}
