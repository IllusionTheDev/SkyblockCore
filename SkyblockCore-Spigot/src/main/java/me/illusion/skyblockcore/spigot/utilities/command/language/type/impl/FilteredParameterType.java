package me.illusion.skyblockcore.spigot.utilities.command.language.type.impl;

import java.util.List;
import me.illusion.cosmos.utilities.text.Placeholder;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.ParameterType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface FilteredParameterType<Type> extends ParameterType<Type> {

    List<Type> getAllValues();

    @Nullable
    List<Placeholder<Player>> createPlaceholders(Object value);

}
