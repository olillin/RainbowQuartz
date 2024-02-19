package dev.hoodieboi.rainbowquartz.plugin.gui.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.kyori.adventure.text.format.TextDecoration.ITALIC
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.Contract

/**
 * An immutable menu that allows items to be inserted into certain slots
 * defined by [insertSlots].
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
     * Inserts an [item] into a [slot] of the menu's inventory.
     * If [item] is null or the same as the item in the slot,
     * the slot will be emptied instead.
     * If [slot] is not in [insertSlots] the call is silently ignored.
     */
    open fun insertItem(slot: Int, item: ItemStack?) {
        if (!isInsertSlot(slot)) return

        val stack: ItemStack? = transformItem(item?.let { ItemStack(it) })
        if (inventory.getItem(slot) == stack) {
            inventory.setItem(slot, null)
        } else {
            inventory.setItem(slot, stack)
        }
    }

    protected open fun isInsertSlot(slot: Int): Boolean = slot >= 0 && slot < inventory.size && insertSlots.contains(slot)

    @EventHandler
    open fun onClickInsertMenu(event: InventoryClickEvent) {
        if (event.isShiftClick) {
            if (event.clickedInventory == inventory) {
                insertItem(event.rawSlot, null)
            } else if (event.currentItem != null) {
                val slot: Int = (insertSlots.firstOrNull { slot ->
                    inventory.getItem(slot) == null
                }.takeIf { it != -1 })
                    ?: insertSlots.lastOrNull()
                    ?: return
                if (inventory.getItem(slot) == transformItem(event.currentItem)) return

                insertItem(slot, event.currentItem)
            }
        } else if ((event.isLeftClick || event.isRightClick)
                    && event.clickedInventory == inventory) {
            insertItem(event.rawSlot, event.cursor)
        } else if (event.click == ClickType.MIDDLE
                && viewer.gameMode == GameMode.CREATIVE
                && (event.cursor == null || event.cursor?.type?.isAir == true)
                && isInsertSlot(event.rawSlot)) {
            viewer.setItemOnCursor(untransformItem(event.currentItem))
        }
    }

    @EventHandler
    open fun onDragInsertMenu(event: InventoryDragEvent) {
        for (slot in event.rawSlots) {
            insertItem(slot, event.oldCursor)
        }
    }

    /**
     * Transform an [item] into one to display in the menu.
     *
     * @see untransformItem
     */
    @Contract("null -> null; !null -> new")
    protected open fun transformItem(item: ItemStack?): ItemStack? {
        if (item == null) return null
        val stack = ItemStack(item)
        val meta: ItemMeta = stack.itemMeta ?: return stack
        meta.lore( (meta.lore() ?: ArrayList<Component>())
                .also { lore ->
                    lore.add(
                            Component.text("Click to remove").color(RED).decoration(ITALIC, false)
                    )
                }
        )
        meta.persistentDataContainer.set(
                NamespacedKey.fromString("rainbowquartz:origin_item")!!,
                PersistentDataType.BYTE_ARRAY,
                ItemStack(item).also {
                    it.amount = it.maxStackSize
                }.serializeAsBytes()
        )
        stack.itemMeta = meta
        stack.amount = 1
        return stack
    }

    /**
     * Untransform an [item] in the menu into what it was before being transformed by.
     *
     * @see transformItem
     */
    @Contract("null -> null; !null -> new")
    protected open fun untransformItem(item: ItemStack?): ItemStack? {
        if (item == null) return null
        val stack = ItemStack(item)
        val meta: ItemMeta = stack.itemMeta ?: return stack
        val bytes: ByteArray = meta.persistentDataContainer.get(
                NamespacedKey.fromString("rainbowquartz:origin_item")!!,
                PersistentDataType.BYTE_ARRAY)
                ?: return stack
        return ItemStack.deserializeBytes(bytes)
    }
}