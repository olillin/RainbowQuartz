package dev.hoodieboi.rainbowquartz.event

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class ItemEventDispatcher(val plugin: Plugin) : Listener {

    fun listen(eventType: Class<Event>) {
        plugin.server.pluginManager.registerEvent(
            eventType,
            this,
            EventPriority.HIGH,
            { _: Listener, event: Event -> onEvent(event) },
            plugin
        )
    }

    private fun onEvent(event: Event) {
        RainbowQuartz.itemManager.getItems().forEach { item ->
            val handlers: Set<PredicatedEventHandler<out Event>>? = item.getHandlerPairs(event::class.java)
            handlers?.forEach {
                it.tryInvoke(event)
            }
        }
    }
}