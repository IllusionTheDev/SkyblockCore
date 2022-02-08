package me.illusion.skyblockcore.bungee.comparison;

import me.illusion.skyblockcore.shared.data.ServerInfo;

import java.util.Comparator;

public class FullFitComparator implements Comparator<ServerInfo> {

    @Override
    public int compare(ServerInfo serverInfo, ServerInfo t1) {
        // fullest first
        if (serverInfo.getIslandCount() == t1.getIslandCount()) {
            return 0;
        }

        return serverInfo.getIslandCount() > t1.getIslandCount() ? -1 : 1;
    }
}
