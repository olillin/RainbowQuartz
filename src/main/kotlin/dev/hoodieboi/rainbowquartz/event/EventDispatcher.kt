package dev.hoodieboi.rainbowquartz.event

import dev.hoodieboi.rainbowquartz.RainbowQuartz
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack

class EventDispatcher : Listener {
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