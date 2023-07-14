package dev.hoodieboi.rainbowquartz.event

import org.bukkit.NamespacedKey
import org.bukkit.event.Event

interface EventContext<in T: Event> {
    fun assembleContexts(event: T): Map<NamespacedKey, Set<EventContext<T>>>
}