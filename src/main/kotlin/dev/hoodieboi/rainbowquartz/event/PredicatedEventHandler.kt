package dev.hoodieboi.rainbowquartz.event

import org.bukkit.event.Event

data class PredicatedEventHandler<T : Event>(val predicate: EventPredicate<T>, val handler: EventHandler<T>) {
    /**
     * Invoke the handler if the predicate succeeds.
     *
     * @param event The event to invoke
     */
    fun tryInvoke(event: T) {
        val item = predicate.getItem(event) ?: return
        handler.onEvent(item, event)
    }
}