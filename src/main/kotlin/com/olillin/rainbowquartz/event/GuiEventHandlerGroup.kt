package com.olillin.rainbowquartz.event

import com.olillin.rainbowquartz.item.Item
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.event.Event

public class GuiEventHandlerGroup<T : Event>(
    override val eventType: Class<out T>,
    override val predicate: GuiEventPredicate<*, T>,
    override val handler: GuiEventHandler<*, T>
) : EventHandlerGroup<T>(eventType, predicate, handler), ConfigurationSerializable {

    override fun serialize(): Map<String, Any> {
        return mapOf(
            "type" to eventType,
            "predicate" to predicate,
            "handler" to handler,
        )
    }

    public companion object {
        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized event handler group
         * @see ConfigurationSerializable
         */
        @Suppress("UNCHECKED_CAST")
        public fun <T: Event> deserialize(args: Map<String, Any>): GuiEventHandlerGroup<*> {
            val section = MemoryConfiguration()
            for ((key, value) in args.entries) {
                section[key] = value
            }

            val eventType: Class<T> = (section.getObject("type", Class::class.java) as? Class<T>)
                ?: throw IllegalArgumentException("Missing or invalid property 'type'")
            val predicate: GuiEventPredicate<*, T> = (section.getObject("predicate", GuiEventPredicate::class.java) as? GuiEventPredicate<*, T>)
                ?: throw IllegalArgumentException("Missing or invalid property 'predicate'")
            val handler: GuiEventHandler<*, T> = (section.getObject("handler", GuiEventHandler::class.java) as? GuiEventHandler<*, T>)
                ?: throw IllegalArgumentException("Missing or invalid property 'handler'")

            return GuiEventHandlerGroup(eventType, predicate, handler)
        }
    }
}