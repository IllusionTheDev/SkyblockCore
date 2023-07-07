package me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.filtered;

import java.util.List;
import java.util.Locale;
import me.illusion.cosmos.utilities.text.Placeholder;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.FilteredParameterType;
import me.illusion.skyblockcore.spigot.utilities.command.language.unit.MinecraftTime;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class TimeParameterType implements FilteredParameterType<MinecraftTime> {

    @Override
    public boolean isType(String input) {
        try {
            MinecraftTime.valueOf(input.toUpperCase(Locale.ROOT));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public MinecraftTime parse(String input) {
        return MinecraftTime.valueOf(input.toUpperCase(Locale.ROOT));
    }

    @Override
    public MinecraftTime getDefaultValue() {
        return MinecraftTime.TICKS;
    }

    @Override
    public List<MinecraftTime> getAllValues() {
        return List.of(MinecraftTime.values());
    }

    @Override
    public @Nullable List<Placeholder<Player>> createPlaceholders(Object input) {
        if (!(isType(input.toString()))) {
            return null;
        }

        MinecraftTime value = parse(input.toString());

        return List.of(
            new Placeholder<Player>("name", beautify(value.name()))
        );
    }

    private String beautify(String input) {
        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1).toLowerCase(Locale.ROOT);
    }
}
