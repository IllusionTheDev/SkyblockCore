package me.illusion.skyblockcore.proxy.instance;

/**
 * Represents a snapshot of a server's data.
 */
public interface ProxyServerData {

    /**
     * Gets the name of the server.
     *
     * @return the name of the server.
     */
    String getName();

    /**
     * Gets the amount of players on the server at the time of the snapshot.
     *
     * @return the amount of players on the server at the time of the snapshot.
     */
    int getPlayerCount();

    /**
     * Gets the amount of islands on the server at the time of the snapshot.
     *
     * @return the amount of islands on the server at the time of the snapshot.
     */
    int getIslandCount();

}
