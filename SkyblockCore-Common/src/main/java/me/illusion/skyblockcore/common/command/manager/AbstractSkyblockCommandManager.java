package me.illusion.skyblockcore.common.command.manager;

import java.util.List;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandContext;
import me.illusion.skyblockcore.common.command.data.SkyblockCommand;
import me.illusion.skyblockcore.common.command.data.SkyblockCommandBuilder;
import me.illusion.skyblockcore.common.command.data.builder.SimpleSkyblockCommandBuilder;
import me.illusion.skyblockcore.common.command.node.ArgumentCommandNode;
import me.illusion.skyblockcore.common.command.node.CommandNode;
import me.illusion.skyblockcore.common.command.structure.CommandTree;
import me.illusion.skyblockcore.common.command.structure.CommandTree.TargetResult;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * Represents an abstract implementation of a SkyblockCommandManager. This implementation provides the command tree logic, and the command handling logic.
 */
public abstract class AbstractSkyblockCommandManager implements SkyblockCommandManager<SkyblockAudience> {

    private final CommandTree commandTree = new CommandTree(this);

    private final SkyblockPlatform platform;

    protected AbstractSkyblockCommandManager(SkyblockPlatform platform) {
        this.platform = platform;
    }

    @Override
    public SkyblockCommandBuilder<SkyblockAudience> newCommand(String name) {
        return new SimpleSkyblockCommandBuilder<>(this, name, SkyblockAudience.class);
    }

    public void registerCommand(SkyblockCommand<?> command) {
        commandTree.registerCommand(command);
    }

    private String createInput(String label, String[] args) {
        return (args.length == 0) ? label : label + " " + String.join(" ", args);
    }

    protected void handle(SkyblockAudience audience, String label, String[] args) {
        handle(audience, createInput(label, args));
    }

    protected <T extends SkyblockAudience> void handle(SkyblockAudience audience, String input) {
        TargetResult result = commandTree.getTargetNode(input);
        SkyblockMessagesFile messages = platform.getMessagesFile();

        if (result == null) {
            messages.sendMessage(audience, "invalid-command");
            return;
        }

        CommandNode node = result.getNode();

        if (!(node instanceof ArgumentCommandNode argumentNode)) {
            return;
        }

        if (!result.isSuccess()) {
            messages.sendMessage(audience, "invalid-argument");
            return;
        }

        CommandContext context = result.getContext();
        SkyblockCommand<T> command = (SkyblockCommand<T>) argumentNode.getCommand();

        Class<T> audienceClass = command.getAudience();

        String permission = command.getPermission();
        boolean hasPermission = permission == null || audience.hasPermission(permission);

        if (!hasPermission) {
            messages.sendMessage(audience, "no-permission");
            return;
        }

        if (!audienceClass.isAssignableFrom(audience.getClass())) {
            messages.sendMessage(audience, "invalid-audience");
            return;
        }

        command.getHandler().handle((T) audience, context);
    }

    protected List<String> tabComplete(SkyblockAudience audience, String label, String[] args) {
        return commandTree.tabComplete(audience, label, args);
    }

    public abstract void registerRoot(String name);

}
