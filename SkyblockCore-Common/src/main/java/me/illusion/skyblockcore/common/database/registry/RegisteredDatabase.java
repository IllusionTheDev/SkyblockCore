package me.illusion.skyblockcore.common.database.registry;

import me.illusion.skyblockcore.common.database.SkyblockDatabase;

public class RegisteredDatabase {

    private final SkyblockDatabaseProvider provider;
    private final String name;

    private SkyblockDatabase database;
    private boolean enabled;

    public RegisteredDatabase(SkyblockDatabaseProvider provider, String name) {
        this.provider = provider;
        this.name = name;
    }

    public SkyblockDatabase getDatabase() {
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

    public SkyblockDatabaseProvider getProvider() {
        return provider;
    }
}
