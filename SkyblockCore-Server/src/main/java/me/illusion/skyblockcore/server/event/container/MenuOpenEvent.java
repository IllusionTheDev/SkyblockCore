package me.illusion.skyblockcore.server.event.container;

import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.server.inventory.data.ContainerView;

public class MenuOpenEvent extends SkyblockEvent {

    private final ContainerView view;

    public MenuOpenEvent(ContainerView view) {
        this.view = view;
    }

    public ContainerView getView() {
        return view;
    }

}
