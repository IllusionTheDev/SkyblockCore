package me.illusion.skyblockcore.common.event.manager;

import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.common.event.listener.SkyblockEventListener;

/**
 * This interface is responsible for managing skyblock events and their listeners.
 */
public interface SkyblockEventManager {

    /**
     * Subscribes a listener to a specific event class.
     *
     * @param eventClass The event class to subscribe to.
     * @param listener   The listener to subscribe.
     * @param <T>        The event type.
     */
    <T extends SkyblockEvent> void subscribe(Class<T> eventClass, SkyblockEventListener<T> listener);

    /**
     * Unsubscribes a listener from a specific event class.
     *
     * @param event The event class to unsubscribe from.
     * @param <T>   The event type.
     */
    <T extends SkyblockEvent> void callEvent(T event);

}
