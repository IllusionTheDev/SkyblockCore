package me.illusion.skyblockcore.common.command.context;

import java.util.List;
import me.illusion.skyblockcore.common.command.context.arg.ProxiedDefaultArgument;

public interface CommandArgument {

    String getName();

    Object parse(CommandContext context);

    default List<String> tabComplete(CommandContext context) {
        return null;
    }

    default CommandArgument orDefault(Object defaultValue) {
        return new ProxiedDefaultArgument(this, defaultValue);
    }

}
