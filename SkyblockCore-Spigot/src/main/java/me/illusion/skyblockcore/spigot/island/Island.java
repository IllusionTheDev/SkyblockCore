package me.illusion.skyblockcore.spigot.island;

import me.illusion.skyblockcore.common.data.IslandData;

public class Island {

    private final IslandData data;

    public Island(IslandData data) {
        this.data = data;
    }

    public IslandData getData() {
        return data;
    }

}
