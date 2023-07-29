package dev.hoodieboi.rainbowquartz.event

import dev.hoodieboi.rainbowquartz.event.handler.EventHandler
import org.bukkit.event.Event

class PredicatedEventHandler<T : Event>(val predicate: EventPredicate<T>, val handler: EventHandler<T>) {
    /**
     * Invoke the handler if the predicate succeeds.
     *
     * @param event The event to invoke
     */
    fun tryInvoke(event: Event) {
        try {
            @Suppress("UNCHECKED_CAST")
            val item = predicate.getItem(event as T) ?: return
            handler.onEvent(item, event)
        } catch (_: ClassCastException) {}
    }
}