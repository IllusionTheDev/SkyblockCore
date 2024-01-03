package me.illusion.skyblockcore.server.inventory;

public class ContainerMetadata {

    private final int width;
    private final int height;

    protected ContainerMetadata(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static ContainerMetadata create(int width, int height) {
        return new ContainerMetadata(width, height);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getSize() {
        return getWidth() * getHeight();
    }

    public boolean isSlotValid(int slot) {
        return slot >= 0 && slot < getSize();
    }

    public int getSlot(int x, int y) {
        return y * getWidth() + x;
    }

}
