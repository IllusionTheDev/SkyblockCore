package me.illusion.skyblockcore.common.registry;

public class SimpleRegistry<T extends Keyed> extends Registry<T> {

    private final Class<T> clazz;

    public SimpleRegistry(Class<T> clazz) {
        this.clazz = clazz;
    }


    @Override
    public Class<T> getObjectType() {
        return clazz;
    }
}
