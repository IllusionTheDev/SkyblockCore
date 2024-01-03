package me.illusion.skyblockcore.server.inventory.data;

import java.util.UUID;
import me.illusion.skyblockcore.server.inventory.ItemContainer;
import me.illusion.skyblockcore.server.inventory.NamedMenu;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

public class ContainerView {

    private final UUID viewId = UUID.randomUUID();

    private final String title;
    private final NamedMenu top;
    private final ItemContainer bottom;
    private final SkyblockPlayer player;

    private ContainerView(String title, NamedMenu top, SkyblockPlayer player) {
        this.title = title;
        this.top = top;
        this.bottom = player.getInventory();
        this.player = player;
    }

    public static ContainerView createView(String title, NamedMenu top, SkyblockPlayer player) {
        return new ContainerView(title, top, player);
    }

    public String getTitle() {
        return title;
    }

    public NamedMenu getTop() {
        return top;
    }

    public ItemContainer getBottom() {
        return bottom;
    }

    public SkyblockPlayer getPlayer() {
        return player;
    }

    public UUID getViewId() {
        return viewId;
    }
}
