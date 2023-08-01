package dev.hoodieboi.rainbowquartz.event

import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.Inventory

object Util {
    /**
     * Get all the ids of rainbow quartz items in inventory
     */
    fun getRainbowQuartzItemsInInventory(inventory: Inventory): Set<NamespacedKey> {
        val ids = HashSet<NamespacedKey>()
        for (itemStack in inventory.iterator()) {
            val id = itemStack.itemMeta.rainbowQuartzId ?: continue
            ids.add(id)
        }
        return ids
    }

    fun playerMainHand(event: PlayerEvent): NamespacedKey? {
        return event.player.inventory.itemInMainHand.itemMeta.rainbowQuartzId
    }

    fun playerOffHand(event: PlayerEvent): NamespacedKey? {
        return event.player.inventory.itemInOffHand.itemMeta.rainbowQuartzId
    }
}