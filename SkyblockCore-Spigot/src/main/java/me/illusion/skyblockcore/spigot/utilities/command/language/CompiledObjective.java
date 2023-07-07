package me.illusion.skyblockcore.spigot.utilities.command.language;

import java.util.ArrayList;
import java.util.List;
import me.illusion.skyblockcore.spigot.utilities.command.language.data.ObjectiveMetadata;
import me.illusion.skyblockcore.spigot.utilities.command.language.parser.ArgumentType;
import me.illusion.skyblockcore.spigot.utilities.command.language.parser.argument.Argument;
import me.illusion.skyblockcore.spigot.utilities.command.language.parser.argument.ParameterArgument;
import org.bukkit.command.CommandSender;

public abstract class CompiledObjective {

    private final ObjectiveMetadata metadata;

    public CompiledObjective(ObjectiveMetadata metadata) {
        this.metadata = metadata;
    }

    public <T> T getParameter(String name) {
        for (Argument<?> argument : metadata.getArguments()) {
            if (!argument.getName().equalsIgnoreCase(name)) {
                continue;
            }

            Object value = argument.getValue();

            if (value instanceof String text) {
                if (!(argument instanceof ParameterArgument<?>)) {
                    continue;
                }

                return (T) ((ParameterArgument<?>) argument).getType().parse(text);
            }

            if (value instanceof List<?> list) {
                List<Object> newList = new ArrayList<>(list);
                for (int i = 0; i < newList.size(); i++) {
                    Object object = newList.get(i);
                    if (object instanceof String text) {
                        if (!(argument instanceof ParameterArgument<?>)) {
                            continue;
                        }

                        newList.set(i, ((ParameterArgument<?>) argument).getType().parse(text));
                    }
                }

                return (T) newList;
            }

            return (T) value;
        }

        throw new IllegalArgumentException("Parameter " + name + " does not exist");
    }

    public boolean hasTag(String tag) {
        for (Argument<?> argument : metadata.getArguments()) {
            if (argument.getArgumentType() != ArgumentType.TAG) {
                continue;
            }
            if (!argument.getName().equalsIgnoreCase(tag)) {
                continue;
            }

            return true;
        }

        return false;
    }

    public abstract void execute(CommandSender sender);

    public String asStringFormat() {
        // We need to reverse parse, so we can get the original syntax, but with the variables replaced
        // So for example if we have a "move [blocking] to {x} {y} {z}" objective, and we have the variables {x: 1, y: 2, z: 3}, and no tags, we need to get "move to 1 2 3"
        // We can do this by looping through the parameters, and replacing the variables in the original syntax
        // So for example, if we have the parameter {x}, we can replace it with the variable x, and then replace the variable x with the value 1
        StringBuilder builder = new StringBuilder();
        for (Argument<?> argument : metadata.getArguments()) {
            if (argument.getArgumentType() == ArgumentType.LIST) {
                List<?> list = (List<?>) argument.getValue();
                for (Object object : list) {
                    builder.append(object).append(" ");
                }
            } else if (argument.getArgumentType() == ArgumentType.TAG) {
                builder.append(argument.getName()).append(" ");
            } else {
                builder.append(argument.getValue()).append(" ");
            }
        }

        return builder.toString().trim();
    }

    public String getName() {
        return metadata.getOriginalSyntax().split(" ")[0];
    }

    public String getOriginalSyntax() {
        return metadata.getOriginalSyntax();
    }

    public boolean isLiteral() {
        return getAllVariableArguments().isEmpty();
    }

    public List<Argument<?>> getArguments() {
        return metadata.getArguments();
    }

    public List<Argument<?>> getVariableArguments() {
        List<Argument<?>> arguments = metadata.getArguments();
        List<Argument<?>> variableArguments = new ArrayList<>();
        for (Argument<?> argument : arguments) {
            if (argument.getArgumentType() == ArgumentType.STRING) {
                continue;
            }

            variableArguments.add(argument);
        }

        return variableArguments;
    }

    public Argument<?> getArgument(String name) {
        for (Argument<?> argument : metadata.getArguments()) {
            if (argument.getName().equalsIgnoreCase(name)) {
                return argument;
            }
        }

        return null;
    }

    public List<Argument<?>> getAllVariableArguments() {
        List<Argument<?>> arguments = metadata.getAllArguments();
        List<Argument<?>> variableArguments = new ArrayList<>();
        for (Argument<?> argument : arguments) {
            if (argument.getArgumentType() == ArgumentType.STRING) {
                continue;
            }

            Argument<?> existingArgument = getArgument(argument.getName());

            // if existingArgument is null, it means it's a tag or optional, so we just add from the all arguments
            if (existingArgument == null) {
                variableArguments.add(argument);
                continue;
            }

            variableArguments.add(existingArgument);
        }

        return variableArguments;
    }

    public List<Argument<?>> getAllArguments() {
        return metadata.getAllArguments();
    }
}
