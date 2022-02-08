package me.illusion.skyblockcore.shared.data;

import lombok.Data;

@Data
public class ServerInfo {

    private final byte islandCount;
    private final byte islandCapacity;
    private final String serverName;
}
