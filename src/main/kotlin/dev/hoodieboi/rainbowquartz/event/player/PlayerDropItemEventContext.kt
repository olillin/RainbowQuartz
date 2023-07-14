package dev.hoodieboi.rainbowquartz.event.player

import dev.hoodieboi.rainbowquartz.event.EventContext
import dev.hoodieboi.rainbowquartz.event.Util
import dev.hoodieboi.rainbowquartz.item.rainbowQuartzId
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerDropItemEvent

enum class PlayerDropItemEventContext : EventContext<PlayerDropItemEvent> {
    DROPPED_ITEM,
    PLAYER_INVENTORY,
    PLAYER_ENDER_CHEST,
    PLAYER_MAINHAND,
    PLAYER_OFFHAND;

    override fun assembleContexts(event: PlayerDropItemEvent): Map<NamespacedKey, Set<EventContext<PlayerDropItemEvent>>> {
        val contexts = HashMap<NamespacedKey, MutableSet<EventContext<PlayerDropItemEvent>>>()

        fun add(id: NamespacedKey, context: EventContext<PlayerDropItemEvent>) {
            if (!contexts.containsKey(id)) {
                contexts[id] = HashSet()
            }
            contexts[id]!!.add(context)
        }

        fun add(ids: Collection<NamespacedKey>, context: EventContext<PlayerDropItemEvent>) {
            for (id in ids) {
                add(id, context)
            }
        }

        // Dropped item
        val droppedItemId = event.itemDrop.itemStack.itemMeta.rainbowQuartzId
        if (droppedItemId != null) {
            add(droppedItemId, DROPPED_ITEM)
        }

        // Player inventory
        add(Util.getRainbowQuartzItemsInInventory(event.player.inventory),
            PLAYER_INVENTORY)

        // Ender chest
        add(Util.getRainbowQuartzItemsInInventory(event.player.enderChest),
            PLAYER_ENDER_CHEST)

        // Main hand
        val playerMainHand = Util.playerMainHand(event)
        if (playerMainHand != null) {
            add(playerMainHand, PLAYER_MAINHAND)
        }

        // Off hand
        val playerOffHand = Util.playerOffHand(event)
        if (playerOffHand != null) {
            add(playerOffHand, PLAYER_OFFHAND)
        }

        return contexts
    }
}