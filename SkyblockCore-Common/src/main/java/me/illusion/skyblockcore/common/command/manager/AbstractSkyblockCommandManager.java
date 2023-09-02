package me.illusion.skyblockcore.common.command.manager;

import java.util.List;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandContext;
import me.illusion.skyblockcore.common.command.data.SkyblockCommand;
import me.illusion.skyblockcore.common.command.data.SkyblockCommandBuilder;
import me.illusion.skyblockcore.common.command.node.ArgumentCommandNode;
import me.illusion.skyblockcore.common.command.node.CommandNode;
import me.illusion.skyblockcore.common.command.structure.CommandTree;
import me.illusion.skyblockcore.common.command.structure.CommandTree.TargetResult;

public abstract class AbstractSkyblockCommandManager<T extends SkyblockAudience> implements SkyblockCommandManager<T> {

    private final CommandTree commandTree = new CommandTree();

    @Override
    public SkyblockCommandBuilder<T> newCommand(String name) {
        return null;
    }

    public void registerCommand(SkyblockCommand<?> command) {
        commandTree.registerCommand(command);
    }

    protected void handle(T audience, String input) {
        TargetResult result = commandTree.getTargetNode(input);

        if (result == null) {
            return;
        }

        CommandNode node = result.getNode();

        if (!(node instanceof ArgumentCommandNode argumentNode)) {
            return;
        }

        CommandContext context = result.getContext();
        SkyblockCommand<T> command = (SkyblockCommand<T>) argumentNode.getCommand();

        Class<? extends T> audienceClass = command.getAudience();

        if (audienceClass.isAssignableFrom(audience.getClass())) {
            command.getHandler().handle(audience, context);
        }
    }

    protected List<String> tabComplete(T audience, String input) {
        return commandTree.tabComplete(audience, input);
    }

    protected abstract void registerRoot(String name);

}
