package dev.hoodieboi.rainbowquartz.event

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import java.lang.reflect.Parameter

class EventDispatcher(val plugin: Plugin) : Listener {
    fun startListening(eventType: Class<Event>) {
        for (method in this.javaClass.declaredMethods) {
            if (method.parameters.size == 1 &&
                    method.parameters[0].type == eventType) {
                plugin.server.pluginManager.registerEvent(
                        eventType,
                        this,
                        EventPriority.HIGH,
                        { listener: Listener, event: Event -> method.invoke(this, eventType.cast(event)) },
                        plugin
                )
            }
        }
    }

    private fun processEvent(itemStack: ItemStack, event: Event) {
        val item = RainbowQuartz.itemManager.getItem(itemStack) ?: return
        val registeredListeners = item.getEventListeners(event::class.java).registeredListeners
        for (listener in registeredListeners) {
            listener.callEvent(event)
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        processEvent(event.itemDrop.itemStack, event)
    }
}