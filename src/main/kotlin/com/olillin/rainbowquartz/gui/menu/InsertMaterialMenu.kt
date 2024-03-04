package com.olillin.rainbowquartz.gui.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

/**
 * An immutable menu that allows materials to be inserted into certain slots
 * defined by [insertSlots].
 * Clicking on a slot with an item will fill that slot with the material of
 * that item and keep the item on the cursor.
 * Clicking on the slot with an item of the same material or no item will
 * remove it from the slot.
 */
public abstract class InsertMaterialMenu : InsertMenu() {
    /**
     * Inserts a [material] into a [slot] of the menu's inventory.
     * If [material] is null or the same as the material in the
     * slot, the slot will be emptied instead.
     * If [slot] is not in [insertSlots] the call is silently ignored.
     *
     * @param transform Whether to apply the [transformItem] method to the item.
     */
    protected fun insertMaterial(slot: Int, material: Material?, transform: Boolean = true) {
        val stack: ItemStack? = if (transform) {
            material?.let {
                transformItem(ItemStack(it))
            }
        } else {
            material?.let {
                ItemStack(it)
            }
        }
        insertItem(slot, stack)
    }

    override fun transformItem(item: ItemStack?): ItemStack? {
        if (item == null) return null
        val stack = ItemStack(item.type, 1)
        val meta: ItemMeta = stack.itemMeta ?: return stack
        meta.persistentDataContainer.set(
            NamespacedKey.fromString("rainbowquartz:origin_item")!!,
            PersistentDataType.BYTE_ARRAY,
            ItemStack(item).also {
                it.amount = it.maxStackSize
            }.serializeAsBytes()
        )
        meta.lore(
            listOf(
                Component.text("Click to remove").color(RED).decoration(TextDecoration.ITALIC, false)
            )
        )
        meta.addItemFlags(*ItemFlag.values())
        stack.itemMeta = meta
        return stack
    }
}