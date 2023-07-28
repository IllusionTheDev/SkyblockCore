package me.illusion.skyblockcore.common.event.listener;

import me.illusion.skyblockcore.common.event.SkyblockEvent;

public interface SkyblockEventListener<T extends SkyblockEvent> {

    void onEvent(T event);

}
