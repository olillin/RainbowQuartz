package com.olillin.rainbowquartz.event

import org.bukkit.event.Event

data class EventHandlerGroup<T : Event>(val eventType: Class<out T>, val predicate: EventPredicate<T>, val handler: EventHandler<T>)
