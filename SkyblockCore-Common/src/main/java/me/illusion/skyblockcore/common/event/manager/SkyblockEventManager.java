package me.illusion.skyblockcore.common.event.manager;

import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.common.event.listener.SkyblockEventListener;

public interface SkyblockEventManager {

    <T extends SkyblockEvent> void subscribe(Class<T> eventClass, SkyblockEventListener<T> listener);

    <T extends SkyblockEvent> void callEvent(T event);

}
