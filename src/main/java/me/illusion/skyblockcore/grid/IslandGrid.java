package me.illusion.skyblockcore.grid;

import java.util.HashMap;
import java.util.Map;

public class IslandGrid {

    private final Map<Integer, GridCell> grid = new HashMap<>();

    public IslandGrid(int width, int height) {
        int centerX = width / 2;
        int centerY = height / 2;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = y * width + x;

                int xPos = x - centerX;
                int yPos = y - centerY;

                grid.put(index, new GridCell(index, xPos, yPos, false));
            }
        }
    }

    public GridCell getFirstCell() {
        return grid.values()
                .stream()
                .filter(cell -> !cell.isOccupied())
                .min((cell1, cell2) -> (int) (Math.hypot(cell1.getXPos(), cell1.getYPos()) - Math.hypot(cell2.getXPos(), cell2.getYPos())))
                .orElse(null);
    }
}
