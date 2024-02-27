package com.olillin.rainbowquartz.event

import com.olillin.rainbowquartz.RainbowQuartz
import com.olillin.rainbowquartz.item.rainbowQuartzId
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
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
        for (item in RainbowQuartz.itemManager.getItems()) {
            item.getEventHandlers(event::class.java).forEach { (_, predicate, handler) ->
                val predicateItem: ItemStack = predicate.getItem(event) ?: return@forEach
                if (predicateItem.itemMeta.rainbowQuartzId == item.key) {
                    handler.onEvent(predicateItem, event)
                }
            }
        }
    }
}