package me.illusion.skyblockcore.common.database;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;

/**
 * This interface represents a template for all databases. Do not implement this interface directly, instead implement one of the sub-interfaces.
 */
public interface SkyblockDatabase {

    /**
     * Obtains the name of this database
     *
     * @return The name
     */
    String getName();

    /**
     * Enables the database
     *
     * @param properties The properties, such as the host, port, username, password, etc.
     * @return A future
     */
    CompletableFuture<Boolean> enable(ReadOnlyConfigurationSection properties);

    /**
     * Flushes the database, this is called when the server is shutting down
     *
     * @return A future which completes when the database is flushed
     */
    CompletableFuture<Void> flush();

    /**
     * Gets the tags of this database. Tags are used to identify the database type. For example, a database that has the tag "FILE" will not be compatible with
     * a sharded setup.
     * <p>
     *
     * @return The tags, or an empty collection if there are no tags
     */
    Collection<SkyblockDatabaseTag> getTags();

    default boolean hasTag(SkyblockDatabaseTag tag) {
        return getTags().contains(tag);
    }


}
