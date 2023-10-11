package me.illusion.skyblockcore.common.databaserewrite.registry;

import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;

public class RegisteredDatabase<T extends SkyblockDatabase> {

    private final SkyblockDatabaseProvider<T> provider;
    private final String name;

    private T database;
    private boolean enabled;

    public RegisteredDatabase(SkyblockDatabaseProvider<T> provider, String name) {
        this.provider = provider;
        this.name = name;
    }

    public T getDatabase() {
        if (database == null) {
            database = provider.getDatabase(name);
        }

        return database;
    }

    public void setSpecifiedType(String type) {
        if (database == null) {
            database = provider.getDatabase(type);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
