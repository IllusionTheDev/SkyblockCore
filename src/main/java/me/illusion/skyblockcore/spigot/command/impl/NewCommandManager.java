package me.illusion.skyblockcore.spigot.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.illusion.skyblockcore.spigot.command.CommandManager;
import me.illusion.skyblockcore.spigot.command.SkyblockCommand;
import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResultFull;
import org.bukkit.command.CommandSender;

import java.util.List;

public class NewCommandManager implements CommandManager {

    private final CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();

    @Override
    public void runTests() {

    }

    @Override
    public void register(SkyblockCommand command) {
        LiteralArgumentBuilder<CommandSender> base = LiteralArgumentBuilder.literal(getBaseCommand(command.getIdentifier()));

        if (command.getIdentifier().equalsIgnoreCase(getBaseCommand(command.getIdentifier()))) {
            base.executes(context -> {
                command.execute(context.getSource());
                return 1;
            });

            dispatcher.register(base);
            return;
        }

        String[] identifiers = command.getIdentifier().split("\\.");

        /*
        for(int i = 1; i < identifiers.length; i++) {
            String identifier = identifiers[i];

            int finalI = i;
            base.then(
                    argument(identifier, string())
                            .suggests((context, builder) -> tabComplete(String.join(".", Arrays.copyOf(identifiers, finalI))))
            )
        }

         */
    }

    @Override
    public ComparisonResultFull fullComparison(String identifier) {
        return null;
    }

    @Override
    public SkyblockCommand get(String identifier) {
        return null;
    }

    @Override
    public List<String> tabComplete(String identifier) {
        return null;
    }

    @Override
    public SkyblockCommand get(String name, String... args) {
        return null;
    }

    private String getBaseCommand(String identifier) {
        int index = identifier.indexOf(".");
        return index == -1 ? identifier : identifier.substring(0, index);
    }
}
