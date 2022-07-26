package me.illusion.skyblockcore.spigot.messaging.responder;

import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketInviteResponse;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.island.impl.LoadedIsland;

public class InviteStatusResponder implements PacketHandler<PacketInviteResponse> {

    private final SkyblockPlugin main;

    public InviteStatusResponder(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketInviteResponse packet) {
        PacketInviteResponse.Response response = packet.getResponse();

        IslandInvite invite = packet.getInvite();

        main.getInviteCache().removeInvite(invite);

        if (response == PacketInviteResponse.Response.INVITE_ACCEPTED) {

            Island island = main.getIslandManager().getPlayerIsland(invite.getSender());

            if (island instanceof LoadedIsland) {
                LoadedIsland loadedIsland = (LoadedIsland) island;
                loadedIsland.getData().addUser(invite.getTarget());
            }
        }
    }
}
