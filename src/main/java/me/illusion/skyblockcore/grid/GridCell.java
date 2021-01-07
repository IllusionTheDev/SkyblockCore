package me.illusion.skyblockcore.grid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class GridCell {

    private final int index;
    private final int xPos;
    private final int yPos;
    @Setter
    private boolean occupied;
}
