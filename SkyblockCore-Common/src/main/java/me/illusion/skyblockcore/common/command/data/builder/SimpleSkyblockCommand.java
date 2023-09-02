package me.illusion.skyblockcore.common.command.data.builder;

import java.util.List;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.data.SkyblockCommand;
import me.illusion.skyblockcore.common.command.data.SkyblockCommandHandler;

public class SimpleSkyblockCommand<T extends SkyblockAudience> implements SkyblockCommand<T> {

    private final List<CommandArgument> arguments;
    private final SkyblockCommandHandler<T> handler;
    private final Class<T> audience;
    private final String permission;

    public SimpleSkyblockCommand(List<CommandArgument> arguments, SkyblockCommandHandler<T> handler, Class<T> audience, String permission) {
        this.arguments = arguments;
        this.handler = handler;
        this.audience = audience;
        this.permission = permission;
    }

    @Override
    public List<CommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public SkyblockCommandHandler<T> getHandler() {
        return handler;
    }

    @Override
    public Class<T> getAudience() {
        return audience;
    }

    @Override
    public String getPermission() {
        return permission;
    }
}
