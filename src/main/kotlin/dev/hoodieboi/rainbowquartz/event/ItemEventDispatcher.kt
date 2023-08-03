package dev.hoodieboi.rainbowquartz.event

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class ItemEventDispatcher(val plugin: Plugin) : Listener {

    fun listen(eventType: Class<out Event>) {
        plugin.server.pluginManager.registerEvent(
            eventType,
            this,
            EventPriority.HIGH,
            { _: Listener, event: Event -> callEvent(event) },
            plugin
        )
    }

    private fun <T : Event> callEvent(event: T) {
        RainbowQuartz.itemManager.getItems().forEach { item ->
            val handlers: Set<PredicatedEventHandler<in T>>? = item.getHandlerPairs(event::class.java)
            handlers?.forEach {
                it.tryInvoke(event)
            }
        }
    }
}