package me.illusion.skyblockcore.spigot.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.illusion.skyblockcore.spigot.island.world.EmptyWorldGenerator;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;

import java.io.File;

@Getter
@AllArgsConstructor
public class IslandDependencies {

    /*
        The empty world generator, seems obvious
     */
    private final EmptyWorldGenerator emptyWorldGenerator;

    /*
        The pasting handler, used to save / load islands from files
     */
    private final PastingHandler pastingHandler;

    /*
       Start schematics, default island on selected format
    */
    private final File[] startSchematic;
}
