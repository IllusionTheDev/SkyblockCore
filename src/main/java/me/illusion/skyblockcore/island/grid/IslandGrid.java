package me.illusion.skyblockcore.island.grid;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IslandGrid {

    private final Map<Integer, GridCell> grid = new HashMap<>();

    public IslandGrid(int width, int height) {
        int centerX = width >> 1;
        int centerY = height >> 1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = y * width + x;

                int xPos = x - centerX;
                int yPos = y - centerY;

                grid.put(index, new GridCell(index, xPos, yPos, false));
            }
        }
    }

    /**
     * Obtains the first available cell
     *
     * @return Available cell closest to middle, NULL if all cells are occupied
     */
    public GridCell getFirstCell() {
        Collection<GridCell> cells = grid.values();
        GridCell lowest = null;
        double lowestDistance = 0;

        for (GridCell cell : cells) {
            if (cell.isOccupied())
                continue;

            if (lowest == null) {
                lowest = cell;
                continue;
            }

            if (cell.equals(lowest))
                continue;

            double distance = Math.hypot(lowest.getXPos(), lowest.getYPos()) - Math.hypot(cell.getXPos(), cell.getYPos());

            if (distance > lowestDistance)
                continue;

            lowest = cell;
            lowestDistance = distance;
        }

        return lowest;
    }
}
