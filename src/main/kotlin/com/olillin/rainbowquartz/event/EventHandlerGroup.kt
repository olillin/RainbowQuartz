package com.olillin.rainbowquartz.event

import org.bukkit.event.Event

public class EventHandlerGroup<T : Event>(
    public val eventType: Class<out T>,
    internal val predicate: EventPredicate<T>,
    internal val handler: EventHandler<T>
) {
    override fun toString(): String =
        "${this::class.simpleName}(eventType=$eventType)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventHandlerGroup<*>

        if (eventType != other.eventType) return false
        if (predicate != other.predicate) return false
        if (handler != other.handler) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eventType.hashCode()
        result = 31 * result + predicate.hashCode()
        result = 31 * result + handler.hashCode()
        return result
    }

    public operator fun component1(): Class<out T> = eventType
    public operator fun component2(): EventPredicate<T> = predicate
    public operator fun component3(): EventHandler<T> = handler
}
