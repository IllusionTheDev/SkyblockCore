package me.illusion.skyblockcore.spigot.utilities.command.command.impl;

import me.illusion.skyblockcore.spigot.utilities.command.language.CompiledObjective;
import me.illusion.skyblockcore.spigot.utilities.command.language.data.ObjectiveMetadata;

public abstract class ExecutionContext extends CompiledObjective { // Just a friendly rename

    public ExecutionContext(ObjectiveMetadata metadata) {
        super(metadata);
    }
}
