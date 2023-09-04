package me.illusion.skyblockcore.common.command.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command node. This is used to build the command tree. This abstract implementation provides the parent-child logic.
 */
public abstract class AbstractCommandNode implements CommandNode {

    private final List<CommandNode> children = new ArrayList<>();
    private CommandNode parent;

    @Override
    public void registerChildren(CommandNode... nodes) {
        for (CommandNode node : nodes) {
            node.setParent(this);
            children.add(node);
        }
    }

    @Override
    public void removeChildren(CommandNode... nodes) {
        for (CommandNode node : nodes) {
            node.setParent(null);
            children.remove(node);
        }
    }

    @Override
    public List<CommandNode> getChildren() {
        return children;
    }

    @Override
    public CommandNode getParent() {
        return parent;
    }

    @Override
    public void setParent(CommandNode parent) {
        this.parent = parent;
    }
}
