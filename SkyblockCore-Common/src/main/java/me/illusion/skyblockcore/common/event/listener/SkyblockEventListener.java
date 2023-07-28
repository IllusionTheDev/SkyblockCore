package me.illusion.skyblockcore.common.event.listener;

import me.illusion.skyblockcore.common.event.SkyblockEvent;

/**
 * This interface is responsible for listening to skyblock events.
 *
 * @param <T> The event type.
 */
public interface SkyblockEventListener<T extends SkyblockEvent> {

    /**
     * This method is called when a skyblock event is called.
     *
     * @param event The event that was called.
     */
    void onEvent(T event);

}
