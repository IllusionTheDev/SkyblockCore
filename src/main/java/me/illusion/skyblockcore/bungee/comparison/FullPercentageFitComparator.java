package me.illusion.skyblockcore.bungee.comparison;

import me.illusion.skyblockcore.shared.data.ServerInfo;

import java.util.Comparator;

public class FullPercentageFitComparator implements Comparator<ServerInfo> {
    @Override
    public int compare(ServerInfo serverInfo, ServerInfo t1) {
        float percentageFull = 1 - (serverInfo.getIslandCount() / (float) serverInfo.getIslandCapacity());
        float percentageFull1 = 1 - (t1.getIslandCount() / (float) t1.getIslandCapacity());

        if (serverInfo.getIslandCapacity() == t1.getIslandCapacity()) {
            if (serverInfo.getIslandCount() == t1.getIslandCount()) {
                return 0;
            }
        }

        return Float.compare(percentageFull1, percentageFull);

    }
}
