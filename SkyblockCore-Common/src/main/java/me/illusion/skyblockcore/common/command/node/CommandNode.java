package me.illusion.skyblockcore.common.command.node;

import java.util.List;
import javax.annotation.Nullable;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.context.arg.LiteralArgument;

public interface CommandNode {

    String getName();

    String getPermission();

    List<CommandNode> getChildren();

    CommandNode getParent();

    void setParent(@Nullable CommandNode node);

    void registerChildren(CommandNode... nodes);

    void removeChildren(CommandNode... nodes);

    default CommandArgument getArgument() {
        return new LiteralArgument(getName());
    }

    Class<? extends SkyblockAudience> getTargetAudience();

}
