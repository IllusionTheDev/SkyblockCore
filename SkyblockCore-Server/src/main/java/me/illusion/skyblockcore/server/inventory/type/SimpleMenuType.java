package me.illusion.skyblockcore.server.inventory.type;

import me.illusion.skyblockcore.server.inventory.ContainerMetadata;

public class SimpleMenuType implements MenuType {

    private final ContainerMetadata metadata;

    private SimpleMenuType(ContainerMetadata metadata) {
        this.metadata = metadata;
    }

    public static SimpleMenuType create(int width, int height) {
        return new SimpleMenuType(ContainerMetadata.create(width, height));
    }

    @Override
    public ContainerMetadata getMetadata() {
        return metadata;
    }
}
