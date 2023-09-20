package me.illusion.skyblockcore.common.database;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public abstract class AbstractSkyblockDatabase implements SkyblockDatabase {

    private final Set<SkyblockDatabaseTag> tags = Sets.newConcurrentHashSet();

    protected void addTag(SkyblockDatabaseTag tag) {
        tags.add(tag);
    }

    @Override
    public Collection<SkyblockDatabaseTag> getTags() {
        return Collections.unmodifiableSet(tags);
    }
}
