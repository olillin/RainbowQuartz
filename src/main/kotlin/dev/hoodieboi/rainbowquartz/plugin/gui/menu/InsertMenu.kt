package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import dev.hoodieboi.rainbowquartz.onlyIf
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

/**
 * An immutable menu that items can be inserted items into defined slots.
 * Clicking on a slot with an item will fill that slot with that item and keep
 * it on the cursor. Clicking on the slot with the same item or no item
 * will remove it from the slot.
 */
abstract class InsertMenu : ImmutableMenu() {
    /**
     * The whitelist of slots that items can be inserted into.
     */
    protected abstract val insertSlots: List<Int>
    /**
     * Inserts an item into an insert slot. If slot is not in insertSlots it is silently ignored.
     * @param slot The inventory slot
     * @param newItem The new item, if null or same as existing item the slot will be emptied
     */
    protected fun insertItem(slot: Int, newItem: ItemStack?) {
        if (!insertSlots.contains(slot)) return
        inventory.setItem(
            slot,
            newItem?.let { item ->
                ItemStack(item).also {
                    it.amount = 1
                } onlyIf { it != inventory.getItem(slot)}
            }
        )
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory is PlayerInventory) return
        insertItem(event.rawSlot, event.cursor)
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        for (slot in event.rawSlots) {
            insertItem(slot, event.oldCursor)
        }
    }
}