package me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.filtered;

import java.util.List;
import java.util.function.Function;
import me.illusion.cosmos.utilities.text.Placeholder;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.FilteredParameterType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ListParameterType<Type> implements FilteredParameterType<Type> {

    private final List<Type> values;
    private final Function<Type, String> identifierMapper;

    public ListParameterType(List<Type> values, Function<Type, String> identifierMapper) {
        this.values = values;
        this.identifierMapper = identifierMapper;
    }

    @Override
    public boolean isType(String input) {
        for (Type value : values) {
            if (identifierMapper.apply(value).equalsIgnoreCase(input)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Type parse(String input) {
        for (Type value : values) {
            if (identifierMapper.apply(value).equalsIgnoreCase(input)) {
                return value;
            }
        }

        return null;
    }

    @Override
    public Type getDefaultValue() {
        return null;
    }

    @Override
    public List<Type> getAllValues() {
        return values;
    }

    @Override
    public @Nullable List<Placeholder<Player>> createPlaceholders(Object value) {
        return null;
    }
}
